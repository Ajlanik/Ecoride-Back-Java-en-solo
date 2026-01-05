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
import jakarta.transaction.Transactional; 
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context; 
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response; 
import jakarta.ws.rs.core.SecurityContext; 
import java.util.List;

/**
 *
 * @author ajlan
 */
@Stateless
@Path("cars")
public class CarFacadeREST extends AbstractFacade<Car> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public CarFacadeREST() {
        super(Car.class);
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
        // Par défaut, une nouvelle voiture n'est pas favorite (sauf si forcé)
        if (entity.getIsFavorite() == null) {
            entity.setIsFavorite(false);
        }
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON) //
    public Car edit(@PathParam("id") Integer id, Car entity){
        Car existingCar = super.find(id);
        if (existingCar == null) return null;

        // Mise à jour des champs existants
        if (entity.getBrand() != null) existingCar.setBrand(entity.getBrand());
        if (entity.getModel() != null) existingCar.setModel(entity.getModel());
        if (entity.getLicensePlate() != null) existingCar.setLicensePlate(entity.getLicensePlate());
        if (entity.getColor() != null) existingCar.setColor(entity.getColor());
        if (entity.getEngine() != null) existingCar.setEngine(entity.getEngine());
        if (entity.getNumberOfSeat() != 0) existingCar.setNumberOfSeat(entity.getNumberOfSeat());
        if (entity.getIsActive() != null) existingCar.setIsActive(entity.getIsActive());

        // --- NOUVEAUX CHAMPS (AJOUTÉS ICI) ---
        if (entity.getPurchaseDate() != null) existingCar.setPurchaseDate(entity.getPurchaseDate());
        if (entity.getInsuranceDate() != null) existingCar.setInsuranceDate(entity.getInsuranceDate());
        // On permet de modifier isFavorite ici, mais c'est mieux d'utiliser la route dédiée
        if (entity.getIsFavorite() != null) existingCar.setIsFavorite(entity.getIsFavorite());
        // -------------------------------------
        
        
        // gestion image
        if (entity.getPicture() != null) existingCar.setPicture(entity.getPicture());
        
        
        
        if (entity.getUser() != null && entity.getUser().getId() != null) {
             User newUser = getEntityManager().find(User.class, entity.getUser().getId());
             if (newUser != null) {
                 existingCar.setUser(newUser);
             }
        }

        super.edit(existingCar);
        return existingCar;
    }

    // --- NOUVELLE MÉTHODE POUR GÉRER LE FAVORI ---
    @POST
    @Path("{id}/favorite")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response setFavorite(@PathParam("id") Integer carId, @Context SecurityContext securityContext) {
        try {
            // 1. Récupérer l'utilisateur connecté via le Token
            if (securityContext.getUserPrincipal() == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            String userEmail = securityContext.getUserPrincipal().getName();
            
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", userEmail)
                    .getResultList().stream().findFirst().orElse(null);

            if (user == null) return Response.status(Response.Status.UNAUTHORIZED).build();

            // 2. Vérifier que la voiture appartient bien à cet utilisateur
            Car car = em.find(Car.class, carId);
            if (car == null || !car.getUser().getId().equals(user.getId())) {
                return Response.status(Response.Status.FORBIDDEN).entity("Voiture introuvable ou non autorisée").build();
            }

            // 3. Remettre TOUTES les voitures de cet user à isFavorite = false
            em.createQuery("UPDATE Car c SET c.isFavorite = false WHERE c.user.id = :userId")
              .setParameter("userId", user.getId())
              .executeUpdate();

            // 4. Mettre celle-ci à true et rafraîchir
            car.setIsFavorite(true);
            em.merge(car); 
            
            // On renvoie la voiture modifiée pour que le Front se mette à jour
            return Response.ok(car).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }
    // ---------------------------------------------

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