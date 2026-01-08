package dto;

import java.io.Serializable;
import jakarta.validation.constraints.NotNull;

/**
 * DTO pour la réception des identifiants de connexion.
 */
public class LoginDTO implements Serializable {

    @NotNull(message = "L'email est obligatoire")
    private String email;

    @NotNull(message = "Le mot de passe est obligatoire")
    private String password;

    public LoginDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}