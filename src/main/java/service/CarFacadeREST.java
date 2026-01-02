/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import entity.Car;
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
import jakarta.ws.rs.core.MediaType;
import java.util.List;

/**
 *
 * @author ajlan
 */
@Stateless
@Path("cars") // 👈 IMPORTANT : L'URL est "cars", pas "carRides"
public class CarFacadeREST extends AbstractFacade<Car> { // 👈 IMPORTANT : On gère <Car>

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public CarFacadeREST() {
        super(Car.class); // 👈 IMPORTANT : On passe la classe Car
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Car entity) {
        // Logique pour rattacher le propriétaire (User)
        if (entity.getUser() != null && entity.getUser().getId() != null) {
            User userEnBase = getEntityManager().find(User.class, entity.getUser().getId());
            entity.setUser(userEnBase);
        }
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Car entity) {
        Car existingCar = super.find(id);
        if (existingCar == null) return;

        // Mise à jour des champs spécifiques à la VOITURE
        if (entity.getBrand() != null) existingCar.setBrand(entity.getBrand());
        if (entity.getModel() != null) existingCar.setModel(entity.getModel());
        if (entity.getLicensePlate() != null) existingCar.setLicensePlate(entity.getLicensePlate());
        if (entity.getColor() != null) existingCar.setColor(entity.getColor());
        
        // Attention aux noms des champs corrigés précédemment
        if (entity.getEngine() != null) existingCar.setEngine(entity.getEngine());
        if (entity.getNumberOfSeat() != 0) existingCar.setNumberOfSeat(entity.getNumberOfSeat());
        if (entity.getIsActive() != null) existingCar.setIsActive(entity.getIsActive());

        // Si le propriétaire change (rare)
        if (entity.getUser() != null && entity.getUser().getId() != null) {
             User newUser = getEntityManager().find(User.class, entity.getUser().getId());
             existingCar.setUser(newUser);
        }

        super.edit(existingCar);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Car find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Car> findAll(@QueryParam("userId") Integer userId) {
        // Filtre par utilisateur si ?userId=X est présent
        if (userId != null) {
            TypedQuery<Car> query = em.createQuery("SELECT c FROM Car c WHERE c.user.id = :uid", Car.class);
            query.setParameter("uid", userId);
            return query.getResultList();
        }
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Car> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}