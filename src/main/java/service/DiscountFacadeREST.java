package service;

import dto.DiscountDTO;
import entity.Discount;
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
import mapper.DiscountMapper;

@Stateless
@Path("discounts")
public class DiscountFacadeREST extends AbstractFacade<Discount> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public DiscountFacadeREST() {
        super(Discount.class);
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public DiscountDTO createAndReturn(Discount entity) {
        super.create(entity);
        return DiscountMapper.toDTO(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public DiscountDTO edit(@PathParam("id") Integer id, Discount entity) {
        super.edit(entity);
        return DiscountMapper.toDTO(super.find(id));
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public DiscountDTO findDTO(@PathParam("id") Integer id) {
        return DiscountMapper.toDTO(super.find(id));
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<DiscountDTO> findAllDTO() {
        return super.findAll().stream()
                .map(DiscountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}