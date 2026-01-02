package be.isl.ecoridebackajlani.model; // Ton package

import java.io.Serializable;

public class Credentials implements Serializable {
    
    private String username; // Le front envoie "username" (qui contient l'email)
    private String password;

    // Getters et Setters (Obligatoire pour que le JSON se convertisse tout seul)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}