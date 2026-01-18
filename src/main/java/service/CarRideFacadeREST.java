// service/CarRideFacadeREST.java
package service;

import dto.CarRideDTO;
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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import mapper.CarRideMapper;

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
        
        // --- LOG DIAGNOSTIC ---
        if (entity.getRoutePath() != null) {
            System.out.println("  RoutePath généré (succès) : " + entity.getRoutePath().toString().substring(0, Math.min(50, entity.getRoutePath().toString().length())) + "...");
        } else {
            System.out.println("  RoutePath est NULL");
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
        System.out.println(" [DEBUG-BACK] GET /carRides/" + id + " appelé.");
        
        CarRide ride = super.find(id);
        
        if (ride != null) {

            try {
                em.refresh(ride); // 
                System.out.println("  Cache non valide !!!!!, données fraiches rechargées depuis la DB.");
            } catch (Exception e) {
                System.out.println("  Impossible de rafraichir l'entité : " + e.getMessage());
            }
            // --------------------------------------------------

            System.out.println(" Trajet trouvé (ID=" + ride.getId() + ")");
            System.out.println(" DB StartLat : " + ride.getStartLat());
            System.out.println(" DB StartLon : " + ride.getStartLon());
            System.out.println(" DB EndLat   : " + ride.getEndLat());
            
            boolean hasGeo = (ride.getRoutePath() != null);
            System.out.println(" RoutePath   : " + (hasGeo ? "PRESENT" : "NULL"));
        } else {
            System.out.println(" Trajet introuvable !");
        }
        
        return ride;
    }

    @GET //pour les admins
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<CarRide> findAll(
            @QueryParam("driverId") Integer driverId,
            @QueryParam("departure") String departure,
            @QueryParam("arrival") String arrival,
            @QueryParam("departureDate") String departureDateStr) { // AJOUT DU PARAMETRE
        
        StringBuilder jpql = new StringBuilder("SELECT c FROM CarRide c WHERE 1=1");
        
        // Construction dynamique de la requête
        if (driverId != null) jpql.append(" AND c.driver.id = :driverId");
        if (departure != null && !departure.isEmpty()) jpql.append(" AND c.departurePlace LIKE :dep");
        if (arrival != null && !arrival.isEmpty()) jpql.append(" AND c.arrivalPlace LIKE :arr");
        
        // AJOUT DE LA CONDITION DATE
        if (departureDateStr != null && !departureDateStr.isEmpty()) {
            jpql.append(" AND c.departureDate = :date");
        }
        
        // Création de la query
        TypedQuery<CarRide> query = em.createQuery(jpql.toString(), CarRide.class);
        
        // Injection des paramètres
        if (driverId != null) query.setParameter("driverId", driverId);
        if (departure != null && !departure.isEmpty()) query.setParameter("dep", "%" + departure + "%");
        if (arrival != null && !arrival.isEmpty()) query.setParameter("arr", "%" + arrival + "%");
        
        // CONVERSION ET INJECTION DE LA DATE
        if (departureDateStr != null && !departureDateStr.isEmpty()) {
            // LocalDate.parse("2025-01-30") fonctionne nativement avec le format standard YYYY-MM-DD
            query.setParameter("date", java.time.LocalDate.parse(departureDateStr));
        }
        
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
    
    @GET
    @Path("search") // pour le user
    @Produces(MediaType.APPLICATION_JSON)
   public List<CarRideDTO> search(
            @QueryParam("departure") String departure,
            @QueryParam("arrival") String arrival,
            @QueryParam("departureDate") String departureDateStr, // <--- CORRECTION ICI (c'était "date")
            @Context SecurityContext securityContext) {

        System.out.println("[DEBUG-BACK] Recherche de trajets (Endpoint /search)");

        // 1. Récupérer l'utilisateur connecté
        String currentUserEmail = null;
        if (securityContext != null && securityContext.getUserPrincipal() != null) {
            currentUserEmail = securityContext.getUserPrincipal().getName();
            System.out.println("  > Demandeur identifié : " + currentUserEmail);
        }

        // 2. Construction de la requête JPQL
        StringBuilder jpql = new StringBuilder("SELECT c FROM CarRide c WHERE 1=1");

        // A. Règle : Pas de trajets passés
        jpql.append(" AND c.departureDate >= :today");

        // B. Règle : Ne pas afficher mes propres trajets
        if (currentUserEmail != null) {
            jpql.append(" AND c.driver.email != :currentUserEmail");
        }

        // C. Filtres de recherche
        if (departure != null && !departure.isEmpty()) {
            jpql.append(" AND LOWER(c.departurePlace) LIKE LOWER(:dep)");
        }
        if (arrival != null && !arrival.isEmpty()) {
            jpql.append(" AND LOWER(c.arrivalPlace) LIKE LOWER(:arr)");
        }
        
        // CORRECTION : On utilise la variable renommée departureDateStr
        if (departureDateStr != null && !departureDateStr.isEmpty()) {
            jpql.append(" AND c.departureDate = :targetDate");
        }
        
        jpql.append(" ORDER BY c.departureDate ASC, c.departureTime ASC");

        // 3. Création de la Query
        TypedQuery<CarRide> query = em.createQuery(jpql.toString(), CarRide.class);

        // 4. Injection des paramètres
        query.setParameter("today", LocalDate.now());
        
        if (currentUserEmail != null) {
            query.setParameter("currentUserEmail", currentUserEmail);
        }
        if (departure != null && !departure.isEmpty()) {
            query.setParameter("dep", "%" + departure + "%");
        }
        if (arrival != null && !arrival.isEmpty()) {
            query.setParameter("arr", "%" + arrival + "%");
        }
        
        // CORRECTION : On parse departureDateStr
        if (departureDateStr != null && !departureDateStr.isEmpty()) {
            try {
                query.setParameter("targetDate", LocalDate.parse(departureDateStr));
            } catch (Exception e) {
                System.err.println("  > Erreur format date : " + departureDateStr);
            }
        }

        // 5. Exécution et transformation en DTO
        List<CarRide> rides = query.getResultList();
        System.out.println("  > Résultats bruts trouvés : " + rides.size());

        return rides.stream()
                .map(CarRideMapper::toDTO)
                .collect(Collectors.toList());
    }
}