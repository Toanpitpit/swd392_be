package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.Review;
import fa.training.car_rental_management.enums.ReviewStatus;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review createReview(Review review);
    Optional<Review> getReviewById(Integer id);
    List<Review> getReviewsByBookingId(Integer bookingId);
    List<Review> getReviewsByAuthorId(Integer authorId);
    List<Review> getReviewsByTargetUserId(Integer targetUserId);
    List<Review> getReviewsByStatus(ReviewStatus status);
    List<Review> getAllReviews();
    Review updateReview(Review review);
    void deleteReview(Integer id);
}

