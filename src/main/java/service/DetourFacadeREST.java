package service;

import dto.DetourDTO;
import entity.Detour;
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
import mapper.DetourMapper;

@Stateless
@Path("detours")
public class DetourFacadeREST extends AbstractFacade<Detour> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public DetourFacadeREST() {
        super(Detour.class);
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public DetourDTO createAndReturn(Detour entity) {
        super.create(entity);
        return DetourMapper.toDTO(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public DetourDTO edit(@PathParam("id") Integer id, Detour entity) {
        super.edit(entity);
        return DetourMapper.toDTO(super.find(id));
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public DetourDTO findDTO(@PathParam("id") Integer id) {
        return DetourMapper.toDTO(super.find(id));
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<DetourDTO> findAllDTO() {
        return super.findAll().stream()
                .map(DetourMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}