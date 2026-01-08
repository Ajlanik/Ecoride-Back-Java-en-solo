package dto;

import java.io.Serializable;
import java.util.Date;

public class ReviewDTO implements Serializable {

    private Integer id;
    private int rating;
    private String comment;
    private String role; // "Driver" ou "Passenger"
    private Date createdAt;
    
    private Integer bookingId; // Juste l'ID suffit
    private UserDTO author;    // DTO complet pour afficher nom/avatar
    private UserDTO target;    // DTO complet pour savoir qui est noté

    public ReviewDTO() {
    }

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }

    public UserDTO getAuthor() { return author; }
    public void setAuthor(UserDTO author) { this.author = author; }

    public UserDTO getTarget() { return target; }
    public void setTarget(UserDTO target) { this.target = target; }
}