package service;

import dto.ReviewDTO;
import entity.Review;
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
import mapper.ReviewMapper;

@Stateless
@Path("reviews")
public class ReviewFacadeREST extends AbstractFacade<Review> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public ReviewFacadeREST() {
        super(Review.class);
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON) // On retourne le DTO créé
    public ReviewDTO createAndReturn(Review entity) {
        super.create(entity);
        return ReviewMapper.toDTO(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public ReviewDTO edit(@PathParam("id") Integer id, Review entity) {
        super.edit(entity);
        return ReviewMapper.toDTO(super.find(id));
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ReviewDTO findDTO(@PathParam("id") Integer id) {
        return ReviewMapper.toDTO(super.find(id));
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<ReviewDTO> findAllDTO() {
        return super.findAll().stream()
                .map(ReviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}