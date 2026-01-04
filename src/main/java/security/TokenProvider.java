package security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import java.security.Key;
import java.util.Date;

@ApplicationScoped
public class TokenProvider {

    // ON DÉCIDE D'UNE CLÉ UNIQUE ET ON S'Y TIENT PARTOUT
    private static final String SECRET_KEY = "MaSuperCleSecreteQuiDoitEtreTresLongueEtSecretePourEcoride";
    
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String createToken(String email, Integer userId, String role) {
        long now = (new Date()).getTime();
        long validity = 3600000 * 24; // 24 heures (pour être tranquille en dev)

        return Jwts.builder()
                .setSubject(email)               // Sujet = Email
                .claim("id", userId)             // Ajout ID
                .claim("role", role)             // Ajout Rôle
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + validity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}