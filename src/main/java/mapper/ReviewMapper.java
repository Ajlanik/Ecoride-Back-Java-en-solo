package mapper;

import dto.ReviewDTO;
import entity.Booking;
import entity.Review;
import entity.User; // N'oublie pas cet import

public class ReviewMapper {

    public static ReviewDTO toDTO(Review review) {
        if (review == null) {
            return null;
        }
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setRole(review.getRole());
        dto.setCreatedAt(review.getCreatedAt());

        if (review.getBookingId() != null) {
            dto.setBookingId(review.getBookingId().getId());
        }
        if (review.getAuthorId() != null) {
            dto.setAuthor(UserMapper.toDTO(review.getAuthorId()));
            // On peut aussi remplir l'ID simple pour info
            dto.setAuthorUserId(review.getAuthorId().getId());
        }
        if (review.getTargetId() != null) {
            dto.setTarget(UserMapper.toDTO(review.getTargetId()));
            dto.setTargetUserId(review.getTargetId().getId());
        }

        return dto;
    }

    public static Review toEntity(ReviewDTO dto) {
        if (dto == null) {
            return null;
        }
        Review review = new Review();
        review.setId(dto.getId());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setRole(dto.getRole());
        review.setCreatedAt(dto.getCreatedAt());

        // Mapping Booking (ID)
        if (dto.getBookingId() != null) {
            review.setBookingId(new Booking(dto.getBookingId()));
        }
        
        // Mapping Author (Priorité à l'objet, sinon l'ID simple)
        if (dto.getAuthor() != null) {
            review.setAuthorId(UserMapper.toEntity(dto.getAuthor()));
        } else if (dto.getAuthorUserId() != null) {
            review.setAuthorId(new User(dto.getAuthorUserId()));
        }

        // Mapping Target (Priorité à l'objet, sinon l'ID simple)
        if (dto.getTarget() != null) {
            review.setTargetId(UserMapper.toEntity(dto.getTarget()));
        } else if (dto.getTargetUserId() != null) {
            review.setTargetId(new User(dto.getTargetUserId()));
        }

        return review;
    }
}