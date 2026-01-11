package mapper;

import dto.BookingDTO;
import entity.Booking;
import entity.CarRide;
import entity.User;

public class BookingMapper {

    public static BookingDTO toDTO(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setSeats(booking.getSeats());
        dto.setPrice(booking.getPrice());
        dto.setCommission(booking.getCommission());
        dto.setTotalPaid(booking.getTotalPaid());
        dto.setStatus(booking.getStatus());
        dto.setCreatedAt(booking.getCreatedAt());

        // ID simple (toujours utile)
        if (booking.getCarRideId() != null) {
            dto.setCarRideId(booking.getCarRideId().getId());
            // NOUVEAU : On ajoute l'objet complet pour l'affichage
            dto.setCarRide(CarRideMapper.toDTO(booking.getCarRideId()));
        }
        
        if (booking.getPassengerId() != null) {
            dto.setPassengerId(booking.getPassengerId().getId());
        }
        
        if (booking.getDetour() != null) {
            dto.setDetour(DetourMapper.toDTO(booking.getDetour()));
        }
        
        if (booking.getPassengerId() != null) {
        dto.setPassengerId(booking.getPassengerId().getId()); 
        

        dto.setPassenger(UserMapper.toDTO(booking.getPassengerId()));
    }
        
        return dto;
    }

    public static Booking toEntity(BookingDTO dto) {
        if (dto == null) {
            return null;
        }
        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setSeats(dto.getSeats());
        booking.setPrice(dto.getPrice());
        booking.setCommission(dto.getCommission());
        booking.setTotalPaid(dto.getTotalPaid());
        booking.setStatus(dto.getStatus());
        booking.setCreatedAt(dto.getCreatedAt());

        // Pour la création, on utilise l'ID
        if (dto.getCarRideId() != null) {
            booking.setCarRideId(new CarRide(dto.getCarRideId()));
        }
        if (dto.getPassengerId() != null) {
            booking.setPassengerId(new User(dto.getPassengerId()));
        }

        return booking;
    }
}