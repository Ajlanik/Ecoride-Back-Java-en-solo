/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbTransient;
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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ajlan
 */
@Entity
@Table(name = "car")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Car.findAll", query = "SELECT c FROM Car c"),
    @NamedQuery(name = "Car.findById", query = "SELECT c FROM Car c WHERE c.id = :id"),
    @NamedQuery(name = "Car.findByBrand", query = "SELECT c FROM Car c WHERE c.brand = :brand"),
    @NamedQuery(name = "Car.findByModel", query = "SELECT c FROM Car c WHERE c.model = :model"),
    @NamedQuery(name = "Car.findByLicensePlate", query = "SELECT c FROM Car c WHERE c.licensePlate = :licensePlate"),
    @NamedQuery(name = "Car.findByColor", query = "SELECT c FROM Car c WHERE c.color = :color"),
    @NamedQuery(name = "Car.findByEngine", query = "SELECT c FROM Car c WHERE c.engine = :engine"),
    @NamedQuery(name = "Car.findByNumberOfSeat", query = "SELECT c FROM Car c WHERE c.numberOfSeat = :numberOfSeat"),
    @NamedQuery(name = "Car.findByIsActive", query = "SELECT c FROM Car c WHERE c.isActive = :isActive"),
    @NamedQuery(name = "Car.findByCreatedAt", query = "SELECT c FROM Car c WHERE c.createdAt = :createdAt")})
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "brand")
    private String brand;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "model")
    private String model;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "license_plate")
    private String licensePlate;
    @Size(max = 50)
    @Column(name = "color")
    private String color;
    @Size(max = 50)
    @Column(name = "engine")
    private String engine;
    @Basic(optional = false)
    @NotNull
    @Column(name = "number_of_seat")
    private int numberOfSeat;
    @Column(name = "picture")
    private String picture;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "purchase_date")
    @JsonbDateFormat("yyyy-MM-dd")
    private LocalDate purchaseDate;

    @Column(name = "insurance_date")
    @JsonbDateFormat("yyyy-MM-dd")
    private LocalDate insuranceDate;

    @Column(name = "is_favorite")
    private Boolean isFavorite = false; // Par défaut à faux
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "car")
    private List<CarRide> rideList;

    public Car() {
    }

    public Car(Integer id) {
        this.id = id;
    }

    public Car(Integer id, String brand, String model, String licensePlate, int numberOfSeat) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
        this.numberOfSeat = numberOfSeat;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public int getNumberOfSeat() {
        return numberOfSeat;
    }

    public void setNumberOfSeat(int numberOfSeat) {
        this.numberOfSeat = numberOfSeat;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @XmlTransient
    @JsonbTransient
    public List<CarRide> getRideList() {
        return rideList;
    }

    public void setRideList(List<CarRide> rideList) {
        this.rideList = rideList;
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
        if (!(object instanceof Car)) {
            return false;
        }
        Car other = (Car) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Car[ id=" + id + " ]";
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getInsuranceDate() {
        return insuranceDate;
    }

    public void setInsuranceDate(LocalDate insuranceDate) {
        this.insuranceDate = insuranceDate;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

}
