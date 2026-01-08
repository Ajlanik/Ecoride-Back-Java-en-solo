package service;

import dto.UserDTO;
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

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}