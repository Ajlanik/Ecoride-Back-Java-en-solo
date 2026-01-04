package be.isl.ecoridebackajlani.service;

import be.isl.ecoridebackajlani.model.Credentials;
import be.isl.ecoridebackajlani.model.SocialLoginRequest;
import entity.User;
import security.TokenProvider; // <--- Import de ta classe security

import jakarta.inject.Inject;  // <--- Pour l'injection
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

@Path("auth")
public class AuthRest {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    // C'EST ICI QUE LA MAGIE OPÈRE : ON INJECTE LE PROVIDER
    @Inject
    private TokenProvider tokenProvider;

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Credentials credentials) {
        try {
            List<User> users = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", credentials.getEmail())
                    .getResultList();

            if (users.isEmpty()) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Email ou mot de passe incorrect").build();
            }
            User user = users.get(0);

            if (!BCrypt.checkpw(credentials.getPassword(), user.getPassword())) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Email ou mot de passe incorrect").build();
            }

            // GÉNÉRATION DU TOKEN VIA LE PROVIDER
            // On passe "RegisteredUser" comme rôle par défaut pour l'instant
            String token = tokenProvider.createToken(user.getEmail(), user.getId(), "RegisteredUser");

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);

            return Response.ok(response).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @POST
    @Path("social")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response socialLogin(SocialLoginRequest request) {
        try {
            String email = null;
            String firstName = "User";
            String providerName = request.getProvider() != null ? request.getProvider() : "Social";
            String lastName = providerName.substring(0, 1).toUpperCase() + providerName.substring(1).toLowerCase();

            // 1. Google
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
            // 2. Facebook
            else if ("facebook".equals(request.getProvider())) {
                email = request.getEmail();
                if (request.getFirstName() != null) firstName = request.getFirstName();
                if (request.getLastName() != null) lastName = request.getLastName();
            }

            if (email == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Email introuvable").build();
            }

            List<User> users = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getResultList();

            User user;
            if (users.isEmpty()) {
                user = new User();
                user.setEmail(email);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                String passwordPrefix = providerName.toUpperCase() + "_"; 
                user.setPassword(passwordPrefix + java.util.UUID.randomUUID().toString());
                user.setRoles("[\"RegisteredUser\"]");
                user.setCredits(0);
                user.setRating(5.0);
                user.setIsActive(true);
                user.setIsVerified(true);
                user.setCreatedAt(new java.util.Date());
                user.setUpdatedAt(new java.util.Date());
                em.persist(user);
                em.flush();
            } else {
                user = users.get(0);
            }

            // GÉNÉRATION DU TOKEN VIA LE PROVIDER
            String token = tokenProvider.createToken(user.getEmail(), user.getId(), "RegisteredUser");

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);

            return Response.ok(response).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }
    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@Context SecurityContext securityContext) {
        try {
            // 1. Récupérer l'email depuis le contexte de sécurité (rempli par JwtAuthFilter)
            Principal principal = securityContext.getUserPrincipal();
            
            if (principal == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            String email = principal.getName(); // Notre filtre a mis l'email ici

            // 2. Chercher l'utilisateur en base
            List<User> users = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getResultList();

            if (users.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            User user = users.get(0);

            // 3. Renvoyer l'utilisateur (sans le token, juste les infos)
            return Response.ok(user).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }
}