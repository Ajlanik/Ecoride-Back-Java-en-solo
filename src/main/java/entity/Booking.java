/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbTransient; // 👈 Import indispensable
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ajlan
 */
@Entity
@Table(name = "booking")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Booking.findAll", query = "SELECT b FROM Booking b"),
    @NamedQuery(name = "Booking.findById", query = "SELECT b FROM Booking b WHERE b.id = :id"),
    @NamedQuery(name = "Booking.findBySeats", query = "SELECT b FROM Booking b WHERE b.seats = :seats"),
    @NamedQuery(name = "Booking.findByPrice", query = "SELECT b FROM Booking b WHERE b.price = :price"),
    @NamedQuery(name = "Booking.findByCommission", query = "SELECT b FROM Booking b WHERE b.commission = :commission"),
    @NamedQuery(name = "Booking.findByTotalPaid", query = "SELECT b FROM Booking b WHERE b.totalPaid = :totalPaid"),
    @NamedQuery(name = "Booking.findByStatus", query = "SELECT b FROM Booking b WHERE b.status = :status"),
    @NamedQuery(name = "Booking.findByCreatedAt", query = "SELECT b FROM Booking b WHERE b.createdAt = :createdAt")})
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "seats")
    private int seats;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "commission")
    private BigDecimal commission;
    @Column(name = "total_paid")
    private BigDecimal totalPaid;
    @Size(max = 20)
    @Column(name = "status")
    private String status;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss") 
    private Date updatedAt;
    
    
    @Column(name = "stripe_payment_intent_id")
private String stripePaymentIntentId;
    
    
    //  CORRECTION : Renommé en 'carRideId' pour correspondre au mappedBy de CarRide.java
    @JoinColumn(name = "ride_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    
    private CarRide carRideId; 
    
    @JoinColumn(name = "passenger_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User passengerId;
    
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "bookingId")
    private Detour detour;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bookingId")
    private List<Review> reviewList;

    public Booking() {
    }

    public Booking(Integer id) {
        this.id = id;
    }

    public Booking(Integer id, int seats) {
        this.id = id;
        this.seats = seats;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

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

    // --- RELATION CARRIDE ---

    @JsonbTransient
    public CarRide getCarRideId() {
        return carRideId;
    }

    // Setter Standard (pour JPA)
    public void setCarRideId(CarRide carRideId) {
        System.out.println("🔍 [DEBUG-ENTITY] Setter STANDARD setCarRideId(Object) appelé. Valeur: " + carRideId);
        this.carRideId = carRideId;
    }

    // Setter "Magique" pour JSON (pour Front avec Integer)
    public void setCarRideId(Integer id) {
        System.out.println("🔍 [DEBUG-ENTITY] Setter MAGIQUE setCarRideId(Integer) appelé. Valeur: " + id);
        if (id != null) {
            this.carRideId = new CarRide(id);
            System.out.println("   -> Nouveau CarRide créé avec ID: " + this.carRideId.getId());
        }
    }
    
    public Integer getRideIdSimple() {
        return carRideId != null ? carRideId.getId() : null;
    }

    // --- RELATION PASSENGER (USER) ---

    //@JsonbTransient
    public User getPassengerId() {
        return passengerId;
    }

    // Setter Standard (pour JPA)
    public void setPassengerId(User passengerId) {
        System.out.println("🔍 [DEBUG-ENTITY] Setter STANDARD setPassengerId(Object) appelé.");
        this.passengerId = passengerId;
    }

    // Setter "Magique" pour JSON (pour Front avec Integer)
    public void setPassengerId(Integer id) {
        System.out.println("🔍 [DEBUG-ENTITY] Setter MAGIQUE setPassengerId(Integer) appelé. Valeur: " + id);
        if (id != null) {
            this.passengerId = new User(id);
        }
    }

    // --- AUTRES RELATIONS ---

    public Detour getDetour() {
        return detour;
    }

    public void setDetour(Detour detour) {
        this.detour = detour;
    }

    @XmlTransient
    @JsonbTransient
    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }

    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    
    
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Booking)) {
            return false;
        }
        Booking other = (Booking) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Booking[ id=" + id + " ]";
    }
}