/*
 * Fichier : service/CarRideFacadeREST.java
 * Ce fichier gère les TRAJETS (Départ, Arrivée, Prix...)
 */
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
@Path("carRides") // URL: /resources/carRides

public class CarRideFacadeREST extends AbstractFacade<CarRide> { // ✅ Nom de classe CORRECT

    
    
    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public CarRideFacadeREST() {
        super(CarRide.class); // ✅ Gère CarRide
    }

    
    private org.locationtech.jts.geom.LineString createLineString(List<List<Double>> coords) {
        if (coords == null || coords.size() < 2) return null;
        
        org.locationtech.jts.geom.GeometryFactory factory = new org.locationtech.jts.geom.GeometryFactory();
        org.locationtech.jts.geom.Coordinate[] coordinates = new org.locationtech.jts.geom.Coordinate[coords.size()];
        
        for (int i = 0; i < coords.size(); i++) {
            // ATTENTION : L'ordre est souvent (Longitude, Latitude) pour les géométries (X, Y)
            // Mais Google/Leaflet envoie souvent (Lat, Lon). 
            // Vérifie tes données. Ici je suppose que le Front envoie [Lat, Lon]
            Double lat = coords.get(i).get(0);
            Double lon = coords.get(i).get(1);
            coordinates[i] = new org.locationtech.jts.geom.Coordinate(lon, lat); // X=Lon, Y=Lat
        }
        return factory.createLineString(coordinates);
    }
    
    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(CarRide entity) {
        // Rattachement Driver
        if (entity.getGeometryCoords() != null) {
            entity.setRoutePath(createLineString(entity.getGeometryCoords()));
        }
        if (entity.getDriver() != null && entity.getDriver().getId() != null) {
            entity.setDriver(em.find(User.class, entity.getDriver().getId()));
        }
        // Rattachement Car
        if (entity.getCar() != null && entity.getCar().getId() != null) {
            entity.setCar(em.find(Car.class, entity.getCar().getId()));
        }
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, CarRide entity) {
        CarRide existingRide = super.find(id);
        if (existingRide == null) return;

        // Mise à jour des champs
        if (entity.getDeparturePlace() != null) existingRide.setDeparturePlace(entity.getDeparturePlace());
        if (entity.getArrivalPlace() != null) existingRide.setArrivalPlace(entity.getArrivalPlace());
        if (entity.getDepartureDate() != null) existingRide.setDepartureDate(entity.getDepartureDate());
        if (entity.getDepartureTime() != null) existingRide.setDepartureTime(entity.getDepartureTime());
        if (entity.getPrice() != null) existingRide.setPrice(entity.getPrice());
        if (entity.getSeatsTotal() != 0) existingRide.setSeatsTotal(entity.getSeatsTotal());
        
        // Relations
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
        return super.find(id);
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