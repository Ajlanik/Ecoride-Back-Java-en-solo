package dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Objet de transfert de données pour l'entité Booking.
 * Évite les cycles de sérialisation entre le trajet et le passager.
 */
public class BookingDTO implements Serializable {

    private Integer id;
    private int seats;
    private BigDecimal price;
    private BigDecimal commission;
    private BigDecimal totalPaid;
    private String status;
    private Date createdAt;
    private Integer carRideId; // Uniquement l'ID pour simplifier le lien
    private Integer passengerId; // Uniquement l'ID du passager

    public BookingDTO() {
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCarRideId() {
        return carRideId;
    }

    public void setCarRideId(Integer carRideId) {
        this.carRideId = carRideId;
    }

    public Integer getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Integer passengerId) {
        this.passengerId = passengerId;
    }
}