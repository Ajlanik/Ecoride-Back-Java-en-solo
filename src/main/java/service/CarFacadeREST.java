package service;

import dto.CarDTO;
import entity.Car;
import entity.User;
import mapper.CarMapper;
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
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context; 
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response; 
import jakarta.ws.rs.core.SecurityContext; 
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Path("cars")
public class CarFacadeREST extends AbstractFacade<Car> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public CarFacadeREST() {
        super(Car.class);
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    // On injecte le SecurityContext pour savoir QUI crée la voiture
    public CarDTO createAndReturn(CarDTO dto, @Context SecurityContext securityContext) {
        
        // 1. Sécurité : On vérifie que l'utilisateur est bien connecté
        if (securityContext.getUserPrincipal() == null) {
            throw new WebApplicationException("Vous devez être connecté pour créer un véhicule", Response.Status.UNAUTHORIZED);
        }
        
        // 2. On récupère l'email depuis le Token JWT
        String email = securityContext.getUserPrincipal().getName();
        
        // 3. On charge l'objet User complet depuis la base de données
        User user;
        try {
            user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                     .setParameter("email", email)
                     .getSingleResult();
        } catch (Exception e) {
            throw new WebApplicationException("Utilisateur introuvable en base", Response.Status.UNAUTHORIZED);
        }

        // 4. Conversion du DTO reçu en Entité Car
        Car entity = CarMapper.toEntity(dto);

        // 5. CRUCIAL : On associe l'utilisateur à la voiture
        // C'est cette ligne qui empêche l'erreur "null value in column user_id"
        entity.setUser(user);
        
        // 6. Initialisations par défaut
        if (entity.getIsFavorite() == null) {
            entity.setIsFavorite(false);
        }
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(new Date());
        }
        entity.setIsActive(true); // Par défaut la voiture est active
        
        // 7. Sauvegarde en base
        super.create(entity);
        
        // 8. Retour du DTO
        return CarMapper.toDTO(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public CarDTO edit(@PathParam("id") Integer id, CarDTO dto) {
        Car existingCar = super.find(id);
        if (existingCar == null) return null;

        // Mise à jour partielle
        if (dto.getBrand() != null) existingCar.setBrand(dto.getBrand());
        if (dto.getModel() != null) existingCar.setModel(dto.getModel());
        if (dto.getLicensePlate() != null) existingCar.setLicensePlate(dto.getLicensePlate());
        if (dto.getColor() != null) existingCar.setColor(dto.getColor());
        if (dto.getEngine() != null) existingCar.setEngine(dto.getEngine());
        if (dto.getNumberOfSeat() != 0) existingCar.setNumberOfSeat(dto.getNumberOfSeat());
        if (dto.getPicture() != null) existingCar.setPicture(dto.getPicture());
        
        if (dto.getPurchaseDate() != null) existingCar.setPurchaseDate(dto.getPurchaseDate());
        if (dto.getInsuranceDate() != null) existingCar.setInsuranceDate(dto.getInsuranceDate());
        if (dto.getIsFavorite() != null) existingCar.setIsFavorite(dto.getIsFavorite());
        
        if (dto.getIsActive() != null) existingCar.setIsActive(dto.getIsActive());

        super.edit(existingCar);
        return CarMapper.toDTO(existingCar);
    }

    // --- GESTION DU FAVORI ---
    @POST
    @Path("{id}/favorite")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response setFavorite(@PathParam("id") Integer carId, @Context SecurityContext securityContext) {
        try {
            if (securityContext.getUserPrincipal() == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            String userEmail = securityContext.getUserPrincipal().getName();
            
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", userEmail)
                    .getResultList().stream().findFirst().orElse(null);

            if (user == null) return Response.status(Response.Status.UNAUTHORIZED).build();

            Car car = em.find(Car.class, carId);
            // Sécurité : on ne modifie que ses propres voitures
            if (car == null || !car.getUser().getId().equals(user.getId())) {
                return Response.status(Response.Status.FORBIDDEN).entity("Voiture introuvable ou non autorisée").build();
            }

            // Reset des autres favoris
            em.createQuery("UPDATE Car c SET c.isFavorite = false WHERE c.user.id = :userId")
              .setParameter("userId", user.getId())
              .executeUpdate();

            // Définir le nouveau favori
            car.setIsFavorite(true);
            em.merge(car); 
            
            return Response.ok(CarMapper.toDTO(car)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public CarDTO findDTO(@PathParam("id") Integer id) {
        return CarMapper.toDTO(super.find(id));
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<CarDTO> findAllDTO(@QueryParam("userId") Integer userId, @Context SecurityContext securityContext) {
        List<Car> cars;
        
        // Si un userId spécifique est demandé
        if (userId != null) {
            TypedQuery<Car> query = em.createQuery("SELECT c FROM Car c WHERE c.user.id = :uid", Car.class);
            query.setParameter("uid", userId);
            cars = query.getResultList();
        } 
        // Sinon, si l'utilisateur est connecté, on renvoie SES voitures par défaut (optionnel mais pratique)
        else if (securityContext.getUserPrincipal() != null) {
             String email = securityContext.getUserPrincipal().getName();
             TypedQuery<Car> query = em.createQuery("SELECT c FROM Car c WHERE c.user.email = :email", Car.class);
             query.setParameter("email", email);
             cars = query.getResultList();
        }
        else {
            // Fallback : toutes les voitures (ou vide selon ta préférence)
            cars = super.findAll();
        }
        return cars.stream().map(CarMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}