// service/BookingFacadeREST.java
package service;

import entity.Booking;
import entity.CarRide;
import entity.User;
// Pense à importer Detour si tu utilises un objet Detour séparé
// import entity.Detour; 
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PathParam;

@Stateless
@Path("bookings")
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
       System.out.println("==============================================");
        System.out.println("🚀 [DEBUG-FACADE] create() DÉMARRÉ");
        
        // Inspection de l'objet reçu
        CarRide cr = entity.getCarRideId();
        System.out.println("📦 [DEBUG-FACADE] Entité Booking reçue : " + entity);
        System.out.println("📦 [DEBUG-FACADE] getCarRideId() est null ? : " + (cr == null));
        
        if (cr != null) {
            System.out.println("📦 [DEBUG-FACADE] Contenu CarRide ID: " + cr.getId());
        } else {
            System.out.println("❌ [DEBUG-FACADE] ALERTE ROUGE : CarRide est NULL avant même le traitement !");
        }
        // --- 🔍 LOG DIAGNOSTIC ---
        if (entity.getDetour() != null) {
            System.out.println("  ✅ Détour REÇU !");
            System.out.println("     - Distance: " + entity.getDetour().getDistance());
            System.out.println("     - Pickup: " + entity.getDetour().getPickupAddress());
            
            // Important : Lier le détour au booking pour la cascade JPA
            // Cela assure que la Foreign Key est bien mise
            entity.getDetour().setBookingId(entity);
        } else {
            System.out.println("  ⚠️ Aucun détour reçu dans l'objet Booking");
        }
        // -------------------------

        // Rattachements standards (CarRide, Passenger)
        if (entity.getCarRideId() != null && entity.getCarRideId().getId() != null) {
            System.out.println("🔄 [DEBUG-FACADE] Tentative de rechargement via EntityManager...");
            entity.setCarRideId(em.find(CarRide.class, entity.getCarRideId().getId()));
        }
        if (entity.getPassengerId() != null && entity.getPassengerId().getId() != null) {
            System.out.println("🚀 [DEBUG-FACADE] Appel super.create(entity)...");
            entity.setPassengerId(em.find(User.class, entity.getPassengerId().getId()));
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