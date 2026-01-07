// service/CarRideFacadeREST.java
package service;

import entity.Car;
import entity.CarRide;
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

@Stateless
@Path("carRides")
public class CarRideFacadeREST extends AbstractFacade<CarRide> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public CarRideFacadeREST() {
        super(CarRide.class);
    }

    private org.locationtech.jts.geom.LineString createLineString(List<List<Double>> coords) {
        if (coords == null || coords.size() < 2) return null;
        
        org.locationtech.jts.geom.GeometryFactory factory = new org.locationtech.jts.geom.GeometryFactory();
        org.locationtech.jts.geom.Coordinate[] coordinates = new org.locationtech.jts.geom.Coordinate[coords.size()];
        
        for (int i = 0; i < coords.size(); i++) {
            Double lat = coords.get(i).get(0);
            Double lon = coords.get(i).get(1);
            coordinates[i] = new org.locationtech.jts.geom.Coordinate(lon, lat);
        }
        return factory.createLineString(coordinates);
    }
    
    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(CarRide entity) {
        System.out.println("[DEBUG-BACK] Création CarRide demandée");
        System.out.println("  - Depart: " + entity.getDeparturePlace());
        System.out.println("  - Arrivee: " + entity.getArrivalPlace());
        
        // --- 🔍 LOG DIAGNOSTIC ---
        if (entity.getRoutePath() != null) {
            System.out.println("  ✅ RoutePath généré (succès) : " + entity.getRoutePath().toString().substring(0, Math.min(50, entity.getRoutePath().toString().length())) + "...");
        } else {
            System.out.println("  ❌ RoutePath est NULL");
        }
        // -------------------------

        if (entity.getGeometryCoords() != null) {
            entity.setRoutePath(createLineString(entity.getGeometryCoords()));
        }
        if (entity.getDriver() != null && entity.getDriver().getId() != null) {
            entity.setDriver(em.find(User.class, entity.getDriver().getId()));
        }
        if (entity.getCar() != null && entity.getCar().getId() != null) {
            entity.setCar(em.find(Car.class, entity.getCar().getId()));
        }
        super.create(entity);
    }

    // ... (Le reste du fichier reste IDENTIQUE à votre version fournie)
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, CarRide entity) {
        CarRide existingRide = super.find(id);
        if (existingRide == null) return;

        if (entity.getDeparturePlace() != null) existingRide.setDeparturePlace(entity.getDeparturePlace());
        if (entity.getArrivalPlace() != null) existingRide.setArrivalPlace(entity.getArrivalPlace());
        if (entity.getDepartureDate() != null) existingRide.setDepartureDate(entity.getDepartureDate());
        if (entity.getDepartureTime() != null) existingRide.setDepartureTime(entity.getDepartureTime());
        if (entity.getPrice() != null) existingRide.setPrice(entity.getPrice());
        if (entity.getSeatsTotal() != 0) existingRide.setSeatsTotal(entity.getSeatsTotal());
        
        if (entity.getStatus() != null) existingRide.setStatus(entity.getStatus());
        
        if (entity.getDriver() != null && entity.getDriver().getId() != null) {
            existingRide.setDriver(em.find(User.class, entity.getDriver().getId()));
        }
        if (entity.getCar() != null && entity.getCar().getId() != null) {
            existingRide.setCar(em.find(Car.class, entity.getCar().getId()));
        }
        super.edit(existingRide);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public CarRide find(@PathParam("id") Integer id) {
        System.out.println("🔍 [DEBUG-BACK] GET /carRides/" + id + " appelé.");
        
        CarRide ride = super.find(id);
        
        if (ride != null) {
            // --- AJOUT CRITIQUE POUR FORCER LA RELECTURE DB ---
            try {
                em.refresh(ride); // <--- C'est la commande magique !
                System.out.println("   🔄 Cache invalidé, données fraîches rechargées depuis la DB.");
            } catch (Exception e) {
                System.out.println("   ⚠️ Impossible de rafraîchir l'entité : " + e.getMessage());
            }
            // --------------------------------------------------

            System.out.println("   ✅ Trajet trouvé (ID=" + ride.getId() + ")");
            System.out.println("   📍 DB StartLat : " + ride.getStartLat());
            System.out.println("   📍 DB StartLon : " + ride.getStartLon());
            System.out.println("   📍 DB EndLat   : " + ride.getEndLat());
            
            boolean hasGeo = (ride.getRoutePath() != null);
            System.out.println("   🗺️ RoutePath   : " + (hasGeo ? "PRÉSENT" : "NULL"));
        } else {
            System.out.println("   ❌ Trajet introuvable !");
        }
        
        return ride;
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<CarRide> findAll(
            @QueryParam("driverId") Integer driverId,
            @QueryParam("departure") String departure,
            @QueryParam("arrival") String arrival) {
        
        StringBuilder jpql = new StringBuilder("SELECT c FROM CarRide c WHERE 1=1");
        if (driverId != null) jpql.append(" AND c.driver.id = :driverId");
        if (departure != null && !departure.isEmpty()) jpql.append(" AND c.departurePlace LIKE :dep");
        if (arrival != null && !arrival.isEmpty()) jpql.append(" AND c.arrivalPlace LIKE :arr");
        
        TypedQuery<CarRide> query = em.createQuery(jpql.toString(), CarRide.class);
        if (driverId != null) query.setParameter("driverId", driverId);
        if (departure != null && !departure.isEmpty()) query.setParameter("dep", "%" + departure + "%");
        if (arrival != null && !arrival.isEmpty()) query.setParameter("arr", "%" + arrival + "%");
        
        return query.getResultList();
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