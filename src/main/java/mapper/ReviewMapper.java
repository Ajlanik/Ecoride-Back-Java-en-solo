package mapper;

import dto.ReviewDTO;
import entity.Booking;
import entity.Review;

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
        }
        if (review.getTargetId() != null) {
            dto.setTarget(UserMapper.toDTO(review.getTargetId()));
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

        if (dto.getBookingId() != null) {
            review.setBookingId(new Booking(dto.getBookingId()));
        }
        if (dto.getAuthor() != null) {
            review.setAuthorId(UserMapper.toEntity(dto.getAuthor()));
        }
        if (dto.getTarget() != null) {
            review.setTargetId(UserMapper.toEntity(dto.getTarget()));
        }

        return review;
    }
}