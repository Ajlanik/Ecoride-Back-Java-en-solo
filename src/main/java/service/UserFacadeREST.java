package service;

import dto.UserDTO;
import dto.UserStatsDTO;
import entity.Booking;
import entity.CarRide;
import entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
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
import mapper.UserMapper;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Service REST pour la gestion des utilisateurs utilisant des DTO.
 */
@Stateless
@Path("users")
public class UserFacadeREST extends AbstractFacade<User> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public UserFacadeREST() {
        super(User.class);
    }

    /**
     * Création d'un utilisateur.
     * Prend en entrée une entité car elle contient le mot de passe nécessaire.
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO createAndReturn(User entity) {
        try {
            // Hachage du mot de passe avant sauvegarde
            if (entity.getPassword() != null) {
                String hashed = BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt());
                entity.setPassword(hashed);
            }
            super.create(entity);
            // On retourne le DTO pour confirmer la création sans renvoyer le hash du mot de passe
            return UserMapper.toDTO(entity);
        } catch (ConstraintViolationException e) {
            e.getConstraintViolations().forEach(violation -> {
                System.out.println(" ERREUR VALIDATION : "
                        + violation.getPropertyPath() + " " + violation.getMessage());
            });
            throw e;
        }
    }

    /**
     * Mise à jour d'un utilisateur via un DTO.
     */
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO edit(@PathParam("id") Integer id, User entity) {
        User existingUser = super.find(id);

        if (existingUser == null) {
            return null;
        }

        // Mise à jour partielle basée sur les champs de l'entité reçue
        if (entity.getFirstName() != null) {
            existingUser.setFirstName(entity.getFirstName());
        }
        if (entity.getLastName() != null) {
            existingUser.setLastName(entity.getLastName());
        }
        if (entity.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(entity.getPhoneNumber());
        }
        if (entity.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(entity.getDateOfBirth());
        }
        if (entity.getBio() != null) {
            existingUser.setBio(entity.getBio());
        }
        if (entity.getNationalId() != null) {
            existingUser.setNationalId(entity.getNationalId());
        }
        if (entity.getAvatar() != null) {
            existingUser.setAvatar(entity.getAvatar());
        }

        // Mise à jour sécurisée du mot de passe si présent
        if (entity.getPassword() != null && !entity.getPassword().isEmpty()) {
            String hashed = BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt());
            existingUser.setPassword(hashed);
        }

        super.edit(existingUser);
        return UserMapper.toDTO(existingUser);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    /**
     * Recherche d'un utilisateur par ID renvoyant un DTO.
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public UserDTO findDTO(@PathParam("id") Integer id) {
        return UserMapper.toDTO(super.find(id));
    }

    /**
     * Récupération de tous les utilisateurs convertis en liste de DTO.
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<UserDTO> findAllDTO() {
        return super.findAll().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }
    
 @GET
    @Path("{id}/stats")
    @Produces(MediaType.APPLICATION_JSON)
    public UserStatsDTO getUserStats(@PathParam("id") Integer id) {
        User user = super.find(id);
        if (user == null) {
            return new UserStatsDTO(0.0, 0, 0.0, 0);
        }

        // Récupération de la note (directement depuis la colonne stockée)
        double finalRating = (user.getRating() != null) ? user.getRating() : 5.0;

        // Initialisation des compteurs
        double totalKm = 0.0;
        int completedRidesCount = 0;

        
        
        //---------------- Faudrait stocker ca dans la db non ??-----------------------
        // Calcul pour les trajets où il est CONDUCTEUR
        if (user.getRideList() != null) {
            for (entity.CarRide ride : user.getRideList()) {
                String status = ride.getStatus();
                // On compte uniquement les trajets terminés
                if (status != null && (status.equalsIgnoreCase("COMPLETED") || status.equalsIgnoreCase("completed"))) {
                    completedRidesCount++;
                    totalKm += parseDistance(ride.getDistance());
                }
            }
        }

        // Calcul pour les réservations où il est PASSAGER
        if (user.getBookingList() != null) {
            for (entity.Booking booking : user.getBookingList()) {
                // On vérifie le statut du TRAJET associé
                entity.CarRide ride = booking.getCarRideId();
                if (ride != null) {
                    String rideStatus = ride.getStatus();
                    if (rideStatus != null && (rideStatus.equalsIgnoreCase("COMPLETED") || rideStatus.equalsIgnoreCase("completed"))) {
                        // On vérifie que la réservation elle-même n'est pas annulée
                        String bookingStatus = booking.getStatus();
                        if (!"CANCELLED".equalsIgnoreCase(bookingStatus) && !"REJECTED".equalsIgnoreCase(bookingStatus)) {
                            completedRidesCount++;
                            totalKm += parseDistance(ride.getDistance());
                        }
                    }
                }
            }
        }

        // Calcul du CO2 (120g/km = 0.12 kg/km)
        double co2 = totalKm * 0.120;

        // Retour du DTO complet
        return new UserStatsDTO(
            finalRating,
            user.getCredits(),
            (double) Math.round(co2 * 10) / 10, // Arrondi à 1 décimale
            completedRidesCount
        );
    }

    // --- nettoyage avant envoit ---
    private double parseDistance(String distStr) {
        if (distStr == null || distStr.isEmpty()) return 0.0;
        try {
            // Nettoie la chaîne (ex: "125 km" -> "125") et remplace virgule par point
            String clean = distStr.replaceAll("[^0-9.,]", "").replace(",", ".");
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}