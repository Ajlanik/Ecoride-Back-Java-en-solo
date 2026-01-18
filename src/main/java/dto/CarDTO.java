package dto;

import java.io.Serializable;
import java.time.LocalDate;

public class CarDTO implements Serializable {

    private Integer id;
    private String brand;
    private String model;
    private String licensePlate; // Correction ici
    private String color;
    private String engine;
    private int numberOfSeat;
    private String picture;      // Correction ici (et non photo)
    private Boolean isFavorite;
    private LocalDate purchaseDate;
    private LocalDate insuranceDate;
    private Boolean isActive; 

    public CarDTO() {
    }

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getEngine() { return engine; }
    public void setEngine(String engine) { this.engine = engine; }

    public int getNumberOfSeat() { return numberOfSeat; }
    public void setNumberOfSeat(int numberOfSeat) { this.numberOfSeat = numberOfSeat; }

    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }

    public Boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(Boolean isFavorite) { this.isFavorite = isFavorite; }
    
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public LocalDate getInsuranceDate() { return insuranceDate; }
    public void setInsuranceDate(LocalDate insuranceDate) { this.insuranceDate = insuranceDate; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}