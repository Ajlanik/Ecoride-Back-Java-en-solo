/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import entity.Booking;
import entity.CarRide; // 👈 Import indispensable
import entity.User;    // 👈 Import indispensable
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

/**
 *
 * @author ajlan
 */
@Stateless
@Path("bookings") // 👈 J'ai simplifié l'URL (plus propre que "entity.booking")
public class BookingFacadeREST extends AbstractFacade<Booking> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public BookingFacadeREST() {
        super(Booking.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Booking entity) {
        
        // 1. Rattacher le Passager (User) existant
        if (entity.getPassengerId() != null && entity.getPassengerId().getId() != null) {
            User passenger = em.find(User.class, entity.getPassengerId().getId());
            entity.setPassengerId(passenger);
        }

        // 2. Rattacher le Trajet (CarRide) existant
        // Attention : On utilise bien getCarRideId() (nouveau nom)
        if (entity.getCarRideId() != null && entity.getCarRideId().getId() != null) {
            CarRide ride = em.find(CarRide.class, entity.getCarRideId().getId());
            entity.setCarRideId(ride);
        }

        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Booking entity) {
        // Logique de mise à jour sécurisée (comme pour les voitures)
        
        Booking existingBooking = super.find(id);
        if (existingBooking == null) return;
        
        // Mise à jour des champs simples
        if (entity.getSeats() != 0) existingBooking.setSeats(entity.getSeats());
        if (entity.getPrice() != null) existingBooking.setPrice(entity.getPrice());
        if (entity.getStatus() != null) existingBooking.setStatus(entity.getStatus());
        if (entity.getCommission() != null) existingBooking.setCommission(entity.getCommission());
        if (entity.getTotalPaid() != null) existingBooking.setTotalPaid(entity.getTotalPaid());

        // Mise à jour des relations (si nécessaire)
        if (entity.getCarRideId() != null && entity.getCarRideId().getId() != null) {
             existingBooking.setCarRideId(em.find(CarRide.class, entity.getCarRideId().getId()));
        }
        
        super.edit(existingBooking);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Booking find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Booking> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Booking> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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