package service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import dto.BookingDTO;
import entity.Booking;
import entity.CarRide;
import entity.Detour;
import entity.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import mapper.BookingMapper;
import mapper.DetourMapper;

@Stateless
@Path("bookings")
public class BookingFacadeREST extends AbstractFacade<Booking> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    @Inject
    private StripeService stripeService; // Injection du service 

    public BookingFacadeREST() {
        super(Booking.class);
    }

    @POST
    @Path("init-payment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response initPayment(BookingDTO bookingRequest) {
        try {
            // On recalcule le prix coté serveur
            CarRide ride = em.find(CarRide.class, bookingRequest.getCarRideId());
            if (ride == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Trajet non trouvé").build();
            }

            BigDecimal totalPrice = ride.getPrice().multiply(new BigDecimal(bookingRequest.getSeats()));

            // Appel à Stripe
            PaymentIntent intent = stripeService.createPaymentIntent(totalPrice, "eur");

            return Response.ok("{\"clientSecret\": \"" + intent.getClientSecret() + "\", \"id\": \"" + intent.getId() + "\"}").build();

        } catch (StripeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Override
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Booking entity) {
        if (entity.getStripePaymentIntentId() == null) {
            throw new WebApplicationException("Le paiement est obligatoire", Response.Status.BAD_REQUEST);
        }
        entity.setStatus("PENDING");
        entity.setCreatedAt(new Date());
        super.create(entity);
    }

    @PUT
    @Path("{id}/status")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response updateStatus(@PathParam("id") Integer id, String newStatus) {
        Booking booking = super.find(id);
        if (booking == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String oldStatus = booking.getStatus();

        try {
            // Si on annule ou refuse, on rembourse
            if (("REJECTED".equals(newStatus) || "CANCELLED".equals(newStatus))
                    && !"REJECTED".equals(oldStatus) && !"CANCELLED".equals(oldStatus)) {

                if (booking.getStripePaymentIntentId() != null) {
                    stripeService.refundPayment(booking.getStripePaymentIntentId());
                }
            }

            booking.setStatus(newStatus);
            super.edit(booking); // Sauvegarde
            return Response.ok().build();

        } catch (StripeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur Stripe: " + e.getMessage()).build();
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public BookingDTO createAndReturn(BookingDTO dto, @Context SecurityContext securityContext) {

        if (securityContext.getUserPrincipal() == null) {
            throw new WebApplicationException("Non autorisé", Response.Status.UNAUTHORIZED);
        }

        String email = securityContext.getUserPrincipal().getName();
        User passenger = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();

        if (dto.getCarRideId() == null) {
            throw new WebApplicationException("ID du trajet manquant", Response.Status.BAD_REQUEST);
        }
        CarRide ride = em.find(CarRide.class, dto.getCarRideId());
        if (ride == null) {
            throw new WebApplicationException("Trajet introuvable", Response.Status.NOT_FOUND);
        }

        Booking entity = new Booking();
        entity.setSeats(dto.getSeats() > 0 ? dto.getSeats() : 1);
        entity.setPrice(dto.getPrice());
        entity.setCommission(dto.getCommission());
        entity.setTotalPaid(dto.getTotalPaid());

        // On définit le statut initial
        entity.setStatus("PENDING");
        entity.setCreatedAt(new Date());

        // On récupère l'ID du paiement depuis le DTO
        entity.setStripePaymentIntentId(dto.getStripePaymentIntentId());

        // Vérification de sécurité obligatoire
        if (entity.getStripePaymentIntentId() == null || entity.getStripePaymentIntentId().isEmpty()) {
            throw new WebApplicationException("Erreur: Le paiement n'a pas été fourni.", Response.Status.BAD_REQUEST);
        }

        entity.setPassengerId(passenger);
        entity.setCarRideId(ride);

        if (dto.getDetour() != null) {
            Detour detourEntity = DetourMapper.toEntity(dto.getDetour());
            detourEntity.setBookingId(entity);
            entity.setDetour(detourEntity);
        }

        super.create(entity); // Sauvegarde en db
        return BookingMapper.toDTO(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public BookingDTO edit(@PathParam("id") Integer id, BookingDTO dto) {
        Booking entity = super.find(id);
        if (entity == null) {
            return null;
        }

        // A DEV !!!!! Si le front change le statut ici vers REJECTED, le remboursement ne se fera pas!!!!
        // Il faut utiliser l'endpoint updateStatus pour ça, ou ajouter la logique ici aussi.
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        super.edit(entity);
        return BookingMapper.toDTO(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public BookingDTO findDTO(@PathParam("id") Integer id) {
        return BookingMapper.toDTO(super.find(id));
    }


    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<BookingDTO> findAllDTO(
            @Context SecurityContext securityContext,
            @QueryParam("carRideId") Integer carRideId
    ) {
        if (securityContext.getUserPrincipal() == null) {
            return List.of();
        }

        String email = securityContext.getUserPrincipal().getName();

        User currentUser = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();

        StringBuilder jpql = new StringBuilder("SELECT b FROM Booking b WHERE (b.passengerId.email = :email OR b.carRideId.driver.email = :email) ");
        if (carRideId != null) {
            jpql.append("AND b.carRideId.id = :carRideId ");
        }

        TypedQuery<Booking> query = em.createQuery(jpql.toString(), Booking.class);
        query.setParameter("email", email);
        if (carRideId != null) {
            query.setParameter("carRideId", carRideId);
        }

        List<Booking> bookings = query.getResultList();

        return bookings.stream().map(b -> {
            BookingDTO dto = BookingMapper.toDTO(b);

            Long count = em.createQuery(
                    "SELECT COUNT(r) FROM Review r WHERE r.bookingId = :booking AND r.authorId = :author", Long.class)
                    .setParameter("booking", b)
                    .setParameter("author", currentUser)
                    .getSingleResult();

            dto.setHasAuthUserRated(count > 0);

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
