package be.isl.ecoridebackajlani.service; // <--- Changement ici

// Il faut maintenant importer ta classe Credentials car elle est dans un autre paquet
import be.isl.ecoridebackajlani.model.Credentials; 
import entity.User; // Vérifie que c'est le bon import pour ton entité User

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("login_check")
public class AuthRest {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Credentials credentials) {
        // ... (Le reste du code ne change pas) ...
        try {
            // ... logique de connexion ...
            List<User> users = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", credentials.getUsername())
                    .getResultList();
            
            if (users.isEmpty() || !users.get(0).getPassword().equals(credentials.getPassword())) {
                 return Response.status(Response.Status.UNAUTHORIZED).entity("Echec connexion").build();
            }
            
            User user = users.get(0);
            Map<String, Object> response = new HashMap<>();
            response.put("token", "fake-jwt-token-123456");
            response.put("user", user);

            return Response.ok(response).build();
            
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }
}