package service;

import dto.BookingDTO;
import entity.Booking;
import entity.CarRide;
import entity.Detour;
import entity.User;
import jakarta.ejb.Stateless;
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

    public BookingFacadeREST() {
        super(Booking.class);
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
        entity.setStatus("PENDING");
        entity.setCreatedAt(new Date());
        
        entity.setPassengerId(passenger);
        entity.setCarRideId(ride);

        if (dto.getDetour() != null) {
            Detour detourEntity = DetourMapper.toEntity(dto.getDetour());
            detourEntity.setBookingId(entity); 
            entity.setDetour(detourEntity);    
        }

        super.create(entity);
        return BookingMapper.toDTO(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public BookingDTO edit(@PathParam("id") Integer id, BookingDTO dto) {
        Booking entity = super.find(id);
        if (entity == null) return null;
        
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

    /**
     * ✅ VERSION SÉCURISÉE FINALE
     * Filtre les réservations selon l'utilisateur connecté.
     * Renvoie :
     * 1. Les réservations où je suis le PASSAGER.
     * 2. Les réservations sur les trajets où je suis le CONDUCTEUR.
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<BookingDTO> findAllDTO(@Context SecurityContext securityContext) {
        
        // Sécurité : si pas de token, on renvoie vide
        if (securityContext.getUserPrincipal() == null) {
            return List.of(); 
        }

        String email = securityContext.getUserPrincipal().getName();

        // La requête magique avec le OR
        TypedQuery<Booking> query = em.createQuery(
            "SELECT b FROM Booking b " +
            "WHERE b.passengerId.email = :email " + 
            "OR b.carRideId.driver.email = :email", 
            Booking.class
        );
        
        query.setParameter("email", email);

        return query.getResultList().stream()
                .map(BookingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}