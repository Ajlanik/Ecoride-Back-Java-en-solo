/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author ajlan
 */
@Entity
@Table(name = "detour")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detour.findAll", query = "SELECT d FROM Detour d"),
    @NamedQuery(name = "Detour.findById", query = "SELECT d FROM Detour d WHERE d.id = :id"),
    @NamedQuery(name = "Detour.findByPickupAddress", query = "SELECT d FROM Detour d WHERE d.pickupAddress = :pickupAddress"),
    @NamedQuery(name = "Detour.findByPickupLat", query = "SELECT d FROM Detour d WHERE d.pickupLat = :pickupLat"),
    @NamedQuery(name = "Detour.findByPickupLon", query = "SELECT d FROM Detour d WHERE d.pickupLon = :pickupLon"),
    @NamedQuery(name = "Detour.findByDropoffAddress", query = "SELECT d FROM Detour d WHERE d.dropoffAddress = :dropoffAddress"),
    @NamedQuery(name = "Detour.findByDropoffLat", query = "SELECT d FROM Detour d WHERE d.dropoffLat = :dropoffLat"),
    @NamedQuery(name = "Detour.findByDropoffLon", query = "SELECT d FROM Detour d WHERE d.dropoffLon = :dropoffLon"),
    @NamedQuery(name = "Detour.findByDistance", query = "SELECT d FROM Detour d WHERE d.distance = :distance"),
    @NamedQuery(name = "Detour.findByDuration", query = "SELECT d FROM Detour d WHERE d.duration = :duration"),
    @NamedQuery(name = "Detour.findByDelayPickup", query = "SELECT d FROM Detour d WHERE d.delayPickup = :delayPickup")})
public class Detour implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 255)
    @Column(name = "pickup_address")
    private String pickupAddress;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "pickup_lat")
    private BigDecimal pickupLat;
    @Column(name = "pickup_lon")
    private BigDecimal pickupLon;
    @Size(max = 255)
    @Column(name = "dropoff_address")
    private String dropoffAddress;
    @Column(name = "dropoff_lat")
    private BigDecimal dropoffLat;
    @Column(name = "dropoff_lon")
    private BigDecimal dropoffLon;
    @Size(max = 50)
    @Column(name = "distance")
    private String distance;
    @Column(name = "duration")
    private Integer duration;
    @Column(name = "delay_pickup")
    private Integer delayPickup;
    @JoinColumn(name = "booking_id", referencedColumnName = "id")
    @OneToOne(optional = false)
    private Booking bookingId;

    public Detour() {
    }

    public Detour(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public BigDecimal getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(BigDecimal pickupLat) {
        this.pickupLat = pickupLat;
    }

    public BigDecimal getPickupLon() {
        return pickupLon;
    }

    public void setPickupLon(BigDecimal pickupLon) {
        this.pickupLon = pickupLon;
    }

    public String getDropoffAddress() {
        return dropoffAddress;
    }

    public void setDropoffAddress(String dropoffAddress) {
        this.dropoffAddress = dropoffAddress;
    }

    public BigDecimal getDropoffLat() {
        return dropoffLat;
    }

    public void setDropoffLat(BigDecimal dropoffLat) {
        this.dropoffLat = dropoffLat;
    }

    public BigDecimal getDropoffLon() {
        return dropoffLon;
    }

    public void setDropoffLon(BigDecimal dropoffLon) {
        this.dropoffLon = dropoffLon;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDelayPickup() {
        return delayPickup;
    }

    public void setDelayPickup(Integer delayPickup) {
        this.delayPickup = delayPickup;
    }

    public Booking getBookingId() {
        return bookingId;
    }

    public void setBookingId(Booking bookingId) {
        this.bookingId = bookingId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Detour)) {
            return false;
        }
        Detour other = (Detour) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Detour[ id=" + id + " ]";
    }
    
}
