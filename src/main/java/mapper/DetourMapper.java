package mapper;

import dto.DetourDTO;
import entity.Booking;
import entity.Detour;

public class DetourMapper {

    public static DetourDTO toDTO(Detour detour) {
        if (detour == null) {
            return null;
        }
        DetourDTO dto = new DetourDTO();
        dto.setId(detour.getId());
        dto.setPickupAddress(detour.getPickupAddress());
        dto.setPickupLat(detour.getPickupLat());
        dto.setPickupLon(detour.getPickupLon());
        dto.setDropoffAddress(detour.getDropoffAddress());
        dto.setDropoffLat(detour.getDropoffLat());
        dto.setDropoffLon(detour.getDropoffLon());
        dto.setDistance(detour.getDistance());
        dto.setDuration(detour.getDuration());
        dto.setDelayPickup(detour.getDelayPickup());

        if (detour.getBookingId() != null) {
            dto.setBookingId(detour.getBookingId().getId());
        }

        return dto;
    }

    public static Detour toEntity(DetourDTO dto) {
        if (dto == null) {
            return null;
        }
        Detour detour = new Detour();
        detour.setId(dto.getId());
        detour.setPickupAddress(dto.getPickupAddress());
        detour.setPickupLat(dto.getPickupLat());
        detour.setPickupLon(dto.getPickupLon());
        detour.setDropoffAddress(dto.getDropoffAddress());
        detour.setDropoffLat(dto.getDropoffLat());
        detour.setDropoffLon(dto.getDropoffLon());
        detour.setDistance(dto.getDistance());
        detour.setDuration(dto.getDuration());
        detour.setDelayPickup(dto.getDelayPickup());

        if (dto.getBookingId() != null) {
            detour.setBookingId(new Booking(dto.getBookingId()));
        }

        return detour;
    }
}