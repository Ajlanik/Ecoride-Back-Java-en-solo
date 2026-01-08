package service;

import dto.BookingDTO;
import entity.Booking;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import mapper.BookingMapper;

/**
 * Service REST pour la gestion des réservations utilisant les DTO.
 */
@Stateless
@Path("bookings")
public class BookingFacadeREST extends AbstractFacade<Booking> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public BookingFacadeREST() {
        super(Booking.class);
    }

    /**
     * Crée une nouvelle réservation et retourne son DTO.
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public BookingDTO createAndReturn(Booking entity) {
        super.create(entity);
        return BookingMapper.toDTO(entity);
    }

    /**
     * Modifie une réservation et retourne le DTO mis à jour.
     */
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public BookingDTO edit(@PathParam("id") Integer id, Booking entity) {
        super.edit(entity);
        return BookingMapper.toDTO(super.find(id));
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    /**
     * Recherche une réservation par ID et retourne son DTO.
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public BookingDTO findDTO(@PathParam("id") Integer id) {
        return BookingMapper.toDTO(super.find(id));
    }

    /**
     * Retourne la liste de toutes les réservations sous forme de DTO.
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<BookingDTO> findAllDTO() {
        return super.findAll().stream()
                .map(BookingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}