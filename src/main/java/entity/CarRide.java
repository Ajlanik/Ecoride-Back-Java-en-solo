/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import converter.GeometryConverter;
import converter.StringListConverter;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

/**
 *
 * @author ajlan
 */
@Entity
@Table(name = "car_ride")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CarRide.findAll", query = "SELECT c FROM CarRide c"),
    @NamedQuery(name = "CarRide.findById", query = "SELECT c FROM CarRide c WHERE c.id = :id"),
    @NamedQuery(name = "CarRide.findByDeparturePlace", query = "SELECT c FROM CarRide c WHERE c.departurePlace = :departurePlace"),
    @NamedQuery(name = "CarRide.findByArrivalPlace", query = "SELECT c FROM CarRide c WHERE c.arrivalPlace = :arrivalPlace"),
    @NamedQuery(name = "CarRide.findByDepartureDate", query = "SELECT c FROM CarRide c WHERE c.departureDate = :departureDate"),
    @NamedQuery(name = "CarRide.findByPrice", query = "SELECT c FROM CarRide c WHERE c.price = :price"),
    @NamedQuery(name = "CarRide.findBySeatsAvailable", query = "SELECT c FROM CarRide c WHERE c.seatsAvailable = :seatsAvailable")
})
public class CarRide implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "departure_place")
    private String departurePlace;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "arrival_place")
    private String arrivalPlace;

    @Basic(optional = false)
    @NotNull
    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Basic(optional = false)
    @NotNull
    @Column(name = "departure_time")
    private LocalTime departureTime;

    // --- CORRECTIONS MAJEURES ICI (MAPPING) ---
    @Column(name = "start_lat") // 👈 Indispensable pour lier à start_lat en DB
    private BigDecimal startLat;

    @Column(name = "start_lon") // 👈 Indispensable
    private BigDecimal startLon;

    @Column(name = "end_lat")   // 👈 Indispensable
    private BigDecimal endLat;

    @Column(name = "end_lon")   // 👈 Indispensable
    private BigDecimal endLon;

    // Mapping vers la colonne 'route_path' qui contient les données
    @Column(name = "route_path", columnDefinition = "geometry(LineString,4326)")
    @Convert(converter = GeometryConverter.class)
    @JsonbTransient // On cache l'objet complexe au JSON par défaut
    private LineString routePath;

    @Size(max = 50)
    @Column(name = "distance")
    private String distance;

    @Column(name = "duration")
    private Integer duration;

    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private BigDecimal price;

    @Basic(optional = false)
    @NotNull
    @Column(name = "seats_total")
    private int seatsTotal;

    @Basic(optional = false)
    @NotNull
    @Column(name = "seats_available")
    private int seatsAvailable;

    @Column(name = "allow_detour")
    private Boolean allowDetour;

    @Column(name = "is_recurring")
    private Boolean isRecurring;

    @Size(max = 2147483647)
    @Column(name = "description")
    private String description;

    @Size(max = 20)
    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private Date createdAt;

    @Column(name = "recurrence_days")
    @Convert(converter = StringListConverter.class)
    private List<String> recurrenceDays;

    @Column(name = "recurrence_end_date")
    private LocalDate recurrenceEndDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "carRideId")
    private List<Booking> bookingList;

    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User driver;

    @JoinColumn(name = "car_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Car car;

    @jakarta.persistence.Transient
    private List<List<Double>> geometryCoords;

    public CarRide() {
    }

    public CarRide(Integer id) {
        this.id = id;
    }

    // --- GETTERS & SETTERS ---
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeparturePlace() {
        return departurePlace;
    }

    public void setDeparturePlace(String departurePlace) {
        this.departurePlace = departurePlace;
    }

    public void setUserId(Integer id) {
        if (id != null) {
            this.driver = new User(id);
        }
    }

    public void setCarId(Integer id) {
        if (id != null) {
            this.car = new Car(id);
        }
    }

    public String getArrivalPlace() {
        return arrivalPlace;
    }

    public void setArrivalPlace(String arrivalPlace) {
        this.arrivalPlace = arrivalPlace;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public BigDecimal getStartLat() {
        return startLat;
    }

    public void setStartLat(BigDecimal startLat) {
        this.startLat = startLat;
    }

    public BigDecimal getStartLon() {
        return startLon;
    }

    public void setStartLon(BigDecimal startLon) {
        this.startLon = startLon;
    }

    public BigDecimal getEndLat() {
        return endLat;
    }

    public void setEndLat(BigDecimal endLat) {
        this.endLat = endLat;
    }

    public BigDecimal getEndLon() {
        return endLon;
    }

    public void setEndLon(BigDecimal endLon) {
        this.endLon = endLon;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getSeatsTotal() {
        return seatsTotal;
    }

    public void setSeatsTotal(int seatsTotal) {
        this.seatsTotal = seatsTotal;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public Boolean getAllowDetour() {
        return allowDetour;
    }

    public void setAllowDetour(Boolean allowDetour) {
        this.allowDetour = allowDetour;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<String> getRecurrenceDays() {
        return recurrenceDays;
    }

    public void setRecurrenceDays(List<String> recurrenceDays) {
        this.recurrenceDays = recurrenceDays;
    }

    public LocalDate getRecurrenceEndDate() {
        return recurrenceEndDate;
    }

    public void setRecurrenceEndDate(LocalDate recurrenceEndDate) {
        this.recurrenceEndDate = recurrenceEndDate;
    }

    @XmlTransient
    
    public List<Booking> getBookingList() {
        return bookingList;
    }

    public void setBookingList(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public Integer getCarId() {
        return car != null ? car.getId() : null;
    }

    public Integer getDriverId() {
        return driver != null ? driver.getId() : null;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CarRide)) {
            return false;
        }
        CarRide other = (CarRide) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entity.CarRide[ id=" + id + " ]";
    }

    public LineString getRoutePath() {
        return routePath;
    }

    public void setRoutePath(LineString routePath) {
        this.routePath = routePath;
    }

    @XmlTransient
    public List<List<Double>> getGeometryCoords() {
        return geometryCoords;
    }

    public void setGeometryCoords(List<List<Double>> geometryCoords) {
        this.geometryCoords = geometryCoords;
    }

    // --- GÉOMÉTRIE JTS <-> JSON ---
    // Cette méthode est utilisée par le mapper JSON pour exposer "geometry" au frontend
    @XmlTransient 
    public List<List<Double>> getGeometry() {
        if (this.routePath == null) {
            return null;
        }
        List<List<Double>> coordinates = new ArrayList<>();
        for (Coordinate coord : this.routePath.getCoordinates()) {
            List<Double> point = new ArrayList<>();
            point.add(coord.y); // Latitude
            point.add(coord.x); // Longitude
            coordinates.add(point);
        }
        return coordinates;
    }

    // Cette méthode est utilisée pour peupler l'entité lors de la réception d'un JSON (création)
    public void setGeometry(List<List<Double>> coordinates) {
        if (coordinates == null || coordinates.isEmpty()) {
            this.routePath = null;
            return;
        }

        Coordinate[] jtsCoords = new Coordinate[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            List<Double> point = coordinates.get(i);
            if (point.size() >= 2) {
                double lat = point.get(0);
                double lon = point.get(1);
                jtsCoords[i] = new Coordinate(lon, lat);
            }
        }

        GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
        this.routePath = gf.createLineString(jtsCoords);
    }
}