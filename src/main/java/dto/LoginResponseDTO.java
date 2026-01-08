package dto;

import java.io.Serializable;

/**
 * DTO pour la réponse d'authentification.
 * Contient le token JWT et les informations de l'utilisateur connecté.
 */
public class LoginResponseDTO implements Serializable {

    private String token;
    private UserDTO user;

    public LoginResponseDTO(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }

    public LoginResponseDTO() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}