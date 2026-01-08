package service;

import be.isl.ecoridebackajlani.model.SocialLoginRequest;
import dto.LoginDTO;
import dto.LoginResponseDTO;
import dto.UserDTO;
import entity.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
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
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import mapper.UserMapper;
import org.mindrot.jbcrypt.BCrypt;
import security.TokenProvider;

@Stateless
@Path("auth")
public class AuthFacadeREST {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    @Inject
    private TokenProvider tokenProvider;

    /**
     * Login classique (Email / Mot de passe)
     */
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginDTO credentials) {
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                          .setParameter("email", credentials.getEmail())
                          .getSingleResult();

            if (user != null && BCrypt.checkpw(credentials.getPassword(), user.getPassword())) {
                String token = tokenProvider.createToken(user.getEmail(), user.getId(), "USER");
                LoginResponseDTO response = new LoginResponseDTO(token, UserMapper.toDTO(user));
                return Response.ok(response).build();
            }
        } catch (NoResultException e) {
            // User not found
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity("Identifiants incorrects").build();
    }

    /**
     * Login Social (Google / Facebook)
     * Récupéré et adapté de l'ancien AuthRest.
     */
    @POST
    @Path("social")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response socialLogin(SocialLoginRequest request) {
        try {
            String email = null;
            String firstName = "User";
            String providerName = request.getProvider() != null ? request.getProvider() : "Social";
            String lastName = providerName.substring(0, 1).toUpperCase() + providerName.substring(1).toLowerCase();

            // 1. Logique Google (Décodage du Token JWT Google sans vérification signature serveur pour l'instant)
            if ("google".equals(request.getProvider()) && request.getToken() != null) {
                String[] parts = request.getToken().split("\\.");
                if (parts.length > 1) {
                    String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
                    Jsonb jsonb = JsonbBuilder.create();
                    Map<String, Object> payloadMap = jsonb.fromJson(payloadJson, Map.class);
                    if (payloadMap.containsKey("email")) email = (String) payloadMap.get("email");
                    if (payloadMap.containsKey("given_name")) firstName = (String) payloadMap.get("given_name");
                    if (payloadMap.containsKey("family_name")) lastName = (String) payloadMap.get("family_name");
                }
            } 
            // 2. Logique Facebook
            else if ("facebook".equals(request.getProvider())) {
                email = request.getEmail();
                if (request.getFirstName() != null) firstName = request.getFirstName();
                if (request.getLastName() != null) lastName = request.getLastName();
            }

            if (email == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Email introuvable dans le token social").build();
            }

            // 3. Recherche ou Création de l'utilisateur
            List<User> users = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getResultList();

            User user;
            if (users.isEmpty()) {
                // Création à la volée
                user = new User();
                user.setEmail(email);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                // Mot de passe aléatoire car géré par Google
                String passwordPrefix = providerName.toUpperCase() + "_"; 
                user.setPassword(BCrypt.hashpw(passwordPrefix + UUID.randomUUID().toString(), BCrypt.gensalt()));
                user.setRoles("[\"RegisteredUser\"]"); // ou "USER" selon votre convention
                user.setCredits(0);
                user.setRating(5.0);
                user.setIsActive(true);
                user.setCreatedAt(new Date());
                
                em.persist(user);
                em.flush(); // Pour avoir l'ID généré tout de suite
            } else {
                user = users.get(0);
            }

            // 4. Génération du Token API
            String token = tokenProvider.createToken(user.getEmail(), user.getId(), "USER");
            
            // 5. Réponse DTO propre
            LoginResponseDTO response = new LoginResponseDTO(token, UserMapper.toDTO(user));
            return Response.ok(response).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Erreur login social").build();
        }
    }

    /**
     * Récupérer l'utilisateur courant via le Token
     */
    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMe(@Context SecurityContext securityContext) {
        Principal principal = securityContext.getUserPrincipal();
        
        if (principal == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                          .setParameter("email", principal.getName())
                          .getSingleResult();
            
            return Response.ok(UserMapper.toDTO(user)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}