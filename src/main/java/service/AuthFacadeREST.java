//package service;
//
//import entity.User;
//import security.TokenProvider;
//import jakarta.ejb.Stateless;
//import jakarta.inject.Inject;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.ws.rs.Consumes;
//import jakarta.ws.rs.GET;
//import jakarta.ws.rs.POST;
//import jakarta.ws.rs.Path;
//import jakarta.ws.rs.Produces;
//import jakarta.ws.rs.core.Context;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//import jakarta.ws.rs.core.SecurityContext;
//import java.security.Principal;
//
//@Stateless
//@Path("auth")
//public class AuthFacadeREST {
//
//    @PersistenceContext(unitName = "my_persistence_unit")
//    private EntityManager em;
//
//    @Inject
//    private TokenProvider tokenProvider;
//
//    // Structure pour recevoir le login du front
//    public static class LoginDTO {
//        public String email;
//        public String password;
//    }
//
//    @POST
//    @Path("login")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response login(LoginDTO credentials) {
//        try {
//            // 1. Chercher l'user par email (Attention : query native ou NamedQuery)
//            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
//                          .setParameter("email", credentials.email)
//                          .getSingleResult();
//
//            // 2. Vérifier le mot de passe (Comparaison simple pour l'instant, HASH plus tard !)
//            if (user != null && user.getPassword().equals(credentials.password)) {
//                
//                // 3. Générer le token
//                String token = tokenProvider.createToken(user.getEmail(), user.getId(), "USER");
//                
//                // 4. Renvoyer le token + l'user (sans password grâce à @JsonbTransient)
//                return Response.ok()
//                        .entity("{\"token\":\"" + token + "\", \"user\": " + 
//                                // Astuce rapide pour renvoyer l'objet User en JSON, 
//                                // l'idéal est un DTO ou laisser Jackson le faire si configuré
//                                jakarta.json.bind.JsonbBuilder.create().toJson(user) + 
//                                "}")
//                        .build();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return Response.status(Response.Status.UNAUTHORIZED).entity("Identifiants incorrects").build();
//    }
//
//    @GET
//    @Path("me")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getMe(@Context SecurityContext securityContext) {
//        Principal principal = securityContext.getUserPrincipal();
//        
//        if (principal == null) {
//            return Response.status(Response.Status.UNAUTHORIZED).build();
//        }
//
//        String email = principal.getName(); // L'email extrait du token par le filtre (étape 4)
//        
//        try {
//            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
//                          .setParameter("email", email)
//                          .getSingleResult();
//            return Response.ok(user).build();
//        } catch (Exception e) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//    }
//}