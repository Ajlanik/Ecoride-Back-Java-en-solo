package service;

import dto.LoginDTO;
import dto.LoginResponseDTO;
import dto.UserDTO;
import entity.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;
import mapper.UserMapper;
import org.mindrot.jbcrypt.BCrypt;
import security.TokenProvider;

/**
 * Service REST pour l'authentification.
 * Gère le login et la récupération de l'utilisateur courant via DTO.
 */
@Stateless
@Path("auth")
public class AuthFacadeREST {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    @Inject
    private TokenProvider tokenProvider;

    /**
     * Endpoint de connexion.
     * Reçoit email/password, vérifie avec BCrypt, et retourne un Token + UserDTO.
     */
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginDTO credentials) {
        try {
            // 1. Recherche de l'utilisateur par email
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                          .setParameter("email", credentials.getEmail())
                          .getSingleResult();

            // 2. Vérification du mot de passe haché
            // On utilise checkpw pour comparer le mot de passe en clair avec le hash en base
            if (user != null && BCrypt.checkpw(credentials.getPassword(), user.getPassword())) {
                
                // 3. Génération du token (On passe "USER" comme rôle par défaut pour l'instant)
                String token = tokenProvider.createToken(user.getEmail(), user.getId(), "USER");
                
                // 4. Conversion de l'utilisateur en DTO pour la réponse
                UserDTO userDTO = UserMapper.toDTO(user);
                
                // 5. Retour de la réponse structurée
                LoginResponseDTO response = new LoginResponseDTO(token, userDTO);
                return Response.ok(response).build();
            }
        } catch (NoResultException e) {
            // Utilisateur non trouvé -> on ne fait rien de spécifique, on renvoie UNAUTHORIZED en bas
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erreur interne").build();
        }

        // Si on arrive ici, c'est que le user n'existe pas ou le mot de passe est faux
        return Response.status(Response.Status.UNAUTHORIZED).entity("Identifiants incorrects").build();
    }

    /**
     * Endpoint pour récupérer les infos de l'utilisateur connecté (basé sur le Token).
     */
    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMe(@Context SecurityContext securityContext) {
        Principal principal = securityContext.getUserPrincipal();
        
        if (principal == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String email = principal.getName(); // L'email a été extrait du token par le filtre JWT
        
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                          .setParameter("email", email)
                          .getSingleResult();
            
            // On retourne le DTO propre
            return Response.ok(UserMapper.toDTO(user)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}