package be.isl.ecoridebackajlani;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        
        // 1. Autoriser explicitement votre Front React
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:5173");
        
        // 2. Gestion des identifiants (Cookies/Tokens)
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        
        // 3. Méthodes et Headers autorisés
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, x-requested-with");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        // --- 4. INDISPENSABLE POUR GOOGLE (Manquant dans votre version actuelle) ---
        responseContext.getHeaders().add("Cross-Origin-Opener-Policy", "same-origin-allow-popups");
        responseContext.getHeaders().add("Cross-Origin-Embedder-Policy", "require-corp");
    }
}