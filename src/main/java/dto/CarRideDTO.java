package dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import jakarta.xml.bind.annotation.XmlTransient; // 👈 Import indispensable

public class CarRideDTO implements Serializable {

    private Integer id;
    private String departurePlace;
    private String arrivalPlace;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private BigDecimal price;
    private int seatsAvailable;
    private int seatsTotal;
    private Integer duration;
    private String distance;
    private String status;
    private String description;
    private Boolean allowDetour;
    private Boolean isRecurring;
    
    // Coordonnées pour l'affichage sur la carte
    private List<List<Double>> geometry; 
    private BigDecimal startLat;
    private BigDecimal startLon;
    private BigDecimal endLat;
    private BigDecimal endLon;

    // Relations complètes
    private UserDTO driver;
    private CarDTO car;

    public CarRideDTO() {
    }

    // Getters et Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDeparturePlace() { return departurePlace; }
    public void setDeparturePlace(String departurePlace) { this.departurePlace = departurePlace; }

    public String getArrivalPlace() { return arrivalPlace; }
    public void setArrivalPlace(String arrivalPlace) { this.arrivalPlace = arrivalPlace; }

    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }

    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getSeatsAvailable() { return seatsAvailable; }
    public void setSeatsAvailable(int seatsAvailable) { this.seatsAvailable = seatsAvailable; }

    public int getSeatsTotal() { return seatsTotal; }
    public void setSeatsTotal(int seatsTotal) { this.seatsTotal = seatsTotal; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public String getDistance() { return distance; }
    public void setDistance(String distance) { this.distance = distance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getAllowDetour() { return allowDetour; }
    public void setAllowDetour(Boolean allowDetour) { this.allowDetour = allowDetour; }

    public Boolean getIsRecurring() { return isRecurring; }
    public void setIsRecurring(Boolean isRecurring) { this.isRecurring = isRecurring; }

    // 🔴 C'EST ICI QUE SE FAIT LA CORRECTION
    // On ajoute @XmlTransient pour que JAXB ignore ce champ complexe (List de List)
    // Cela corrige l'erreur "java.util.List est une interface..."
    @XmlTransient 
    public List<List<Double>> getGeometry() { return geometry; }
    public void setGeometry(List<List<Double>> geometry) { this.geometry = geometry; }

    public BigDecimal getStartLat() { return startLat; }
    public void setStartLat(BigDecimal startLat) { this.startLat = startLat; }

    public BigDecimal getStartLon() { return startLon; }
    public void setStartLon(BigDecimal startLon) { this.startLon = startLon; }

    public BigDecimal getEndLat() { return endLat; }
    public void setEndLat(BigDecimal endLat) { this.endLat = endLat; }

    public BigDecimal getEndLon() { return endLon; }
    public void setEndLon(BigDecimal endLon) { this.endLon = endLon; }

    public UserDTO getDriver() { return driver; }
    public void setDriver(UserDTO driver) { this.driver = driver; }

    public CarDTO getCar() { return car; }
    public void setCar(CarDTO car) { this.car = car; }
}