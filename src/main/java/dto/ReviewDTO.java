package dto;

import java.io.Serializable;
import java.util.Date;

public class ReviewDTO implements Serializable {

    private Integer id;
    private int rating;
    private String comment;
    private String role;
    private Date createdAt;

    private Integer bookingId;

    // Ajout des champs d'entrée (Input)
    private Integer authorUserId;
    private Integer targetUserId;

    // Objets complets pour la sortie (Output)
    private UserDTO author;
    private UserDTO target;

    public ReviewDTO() {
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    // Nouveaux Getters/Setters pour les IDs d'entrée
    public Integer getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(Integer authorUserId) {
        this.authorUserId = authorUserId;
    }

    public Integer getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Integer targetUserId) {
        this.targetUserId = targetUserId;
    }

    public UserDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO author) {
        this.author = author;
    }

    public UserDTO getTarget() {
        return target;
    }

    public void setTarget(UserDTO target) {
        this.target = target;
    }
}
