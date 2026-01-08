package mapper;

import dto.CarRideDTO;
import entity.CarRide;

public class CarRideMapper {

    public static CarRideDTO toDTO(CarRide ride) {
        if (ride == null) {
            return null;
        }
        CarRideDTO dto = new CarRideDTO();
        dto.setId(ride.getId());
        dto.setDeparturePlace(ride.getDeparturePlace());
        dto.setArrivalPlace(ride.getArrivalPlace());
        dto.setDepartureDate(ride.getDepartureDate());
        dto.setDepartureTime(ride.getDepartureTime());
        dto.setPrice(ride.getPrice());
        dto.setSeatsAvailable(ride.getSeatsAvailable());
        dto.setSeatsTotal(ride.getSeatsTotal());
        dto.setDuration(ride.getDuration());
        dto.setDistance(ride.getDistance());
        dto.setStatus(ride.getStatus());
        dto.setDescription(ride.getDescription());
        dto.setAllowDetour(ride.getAllowDetour());
        dto.setIsRecurring(ride.getIsRecurring());

        // Mapping des coordonnées GPS
        dto.setStartLat(ride.getStartLat());
        dto.setStartLon(ride.getStartLon());
        dto.setEndLat(ride.getEndLat());
        dto.setEndLon(ride.getEndLon());
        
        // Récupération de la géométrie complexe via la méthode helper de l'entité
        dto.setGeometry(ride.getGeometry());

        // Mapping des relations
        if (ride.getDriver() != null) {
            dto.setDriver(UserMapper.toDTO(ride.getDriver()));
        }
        if (ride.getCar() != null) {
            dto.setCar(CarMapper.toDTO(ride.getCar()));
        }

        return dto;
    }

    public static CarRide toEntity(CarRideDTO dto) {
        if (dto == null) {
            return null;
        }
        CarRide ride = new CarRide();
        ride.setId(dto.getId());
        ride.setDeparturePlace(dto.getDeparturePlace());
        ride.setArrivalPlace(dto.getArrivalPlace());
        ride.setDepartureDate(dto.getDepartureDate());
        ride.setDepartureTime(dto.getDepartureTime());
        ride.setPrice(dto.getPrice());
        ride.setSeatsAvailable(dto.getSeatsAvailable());
        ride.setSeatsTotal(dto.getSeatsTotal());
        ride.setDuration(dto.getDuration());
        ride.setDistance(dto.getDistance());
        ride.setStatus(dto.getStatus());
        ride.setDescription(dto.getDescription());
        ride.setAllowDetour(dto.getAllowDetour());
        ride.setIsRecurring(dto.getIsRecurring());
        
        ride.setStartLat(dto.getStartLat());
        ride.setStartLon(dto.getStartLon());
        ride.setEndLat(dto.getEndLat());
        ride.setEndLon(dto.getEndLon());
        
        // Pour la géométrie, l'entité a une méthode setGeometry qui convertit la List en LineString
        if (dto.getGeometry() != null) {
            ride.setGeometry(dto.getGeometry());
        }

        // Note: Pour driver et car, on gère généralement l'association via ID dans le service
        // ou on reconstruit partiellement si nécessaire
        if (dto.getDriver() != null) {
            ride.setDriver(UserMapper.toEntity(dto.getDriver()));
        }
        if (dto.getCar() != null) {
            ride.setCar(CarMapper.toEntity(dto.getCar()));
        }

        return ride;
    }
}