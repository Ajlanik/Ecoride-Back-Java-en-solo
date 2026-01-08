package mapper;

import dto.BookingDTO;
import entity.Booking;
import entity.CarRide;
import entity.User;

/**
 * Classe utilitaire pour convertir les entités Booking en DTO et inversement.
 */
public class BookingMapper {

    /**
     * Convertit une entité Booking vers un BookingDTO.
     */
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

        // Extraction des IDs des relations
        if (booking.getCarRideId() != null) {
            dto.setCarRideId(booking.getCarRideId().getId());
        }
        if (booking.getPassengerId() != null) {
            dto.setPassengerId(booking.getPassengerId().getId());
        }
        
        return dto;
    }

    /**
     * Convertit un BookingDTO vers une entité Booking.
     */
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

        // Reconstruction des objets liés par ID (objets partiels pour JPA)
        if (dto.getCarRideId() != null) {
            booking.setCarRideId(new CarRide(dto.getCarRideId()));
        }
        if (dto.getPassengerId() != null) {
            booking.setPassengerId(new User(dto.getPassengerId()));
        }

        return booking;
    }
}