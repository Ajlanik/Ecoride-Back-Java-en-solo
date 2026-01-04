package security;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

@Provider
@Priority(Priorities.AUTHENTICATION) // S'exécute avant ton code métier
public class JwtAuthFilter implements ContainerRequestFilter {

    @Inject
    private TokenProvider tokenProvider;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        
        // 1. Récupérer le header Authorization
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // 2. Vérifier s'il est valide (Bearer ...)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Enlever "Bearer "

            // 3. Valider le token
            if (tokenProvider.validateToken(token)) {
                String email = tokenProvider.getEmailFromToken(token);

                // 4. Dire à l'appli "C'est bon, c'est cet utilisateur !"
                final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
                requestContext.setSecurityContext(new SecurityContext() {
                    @Override
                    public Principal getUserPrincipal() {
                        return () -> email;
                    }
                    @Override
                    public boolean isUserInRole(String role) { return true; }
                    @Override
                    public boolean isSecure() { return currentSecurityContext.isSecure(); }
                    @Override
                    public String getAuthenticationScheme() { return "Bearer"; }
                });
            } else {
                // Token invalide -> 401
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }
        }
        // Si pas de header, on laisse passer (pour les pages publiques comme Login)
        // C'est tes @RolesAllowed ou la logique du code qui bloquera si besoin
    }
}