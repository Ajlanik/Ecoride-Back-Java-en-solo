package dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class DetourDTO implements Serializable {

    private Integer id;
    private String pickupAddress;
    private BigDecimal pickupLat;
    private BigDecimal pickupLon;
    private String dropoffAddress;
    private BigDecimal dropoffLat;
    private BigDecimal dropoffLon;
    private String distance;
    private Integer duration;
    private Integer delayPickup;
    private Integer bookingId;

    public DetourDTO() {
    }

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public BigDecimal getPickupLat() { return pickupLat; }
    public void setPickupLat(BigDecimal pickupLat) { this.pickupLat = pickupLat; }

    public BigDecimal getPickupLon() { return pickupLon; }
    public void setPickupLon(BigDecimal pickupLon) { this.pickupLon = pickupLon; }

    public String getDropoffAddress() { return dropoffAddress; }
    public void setDropoffAddress(String dropoffAddress) { this.dropoffAddress = dropoffAddress; }

    public BigDecimal getDropoffLat() { return dropoffLat; }
    public void setDropoffLat(BigDecimal dropoffLat) { this.dropoffLat = dropoffLat; }

    public BigDecimal getDropoffLon() { return dropoffLon; }
    public void setDropoffLon(BigDecimal dropoffLon) { this.dropoffLon = dropoffLon; }

    public String getDistance() { return distance; }
    public void setDistance(String distance) { this.distance = distance; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Integer getDelayPickup() { return delayPickup; }
    public void setDelayPickup(Integer delayPickup) { this.delayPickup = delayPickup; }

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }
}