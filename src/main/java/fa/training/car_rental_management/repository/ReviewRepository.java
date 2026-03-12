package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.Review;
import fa.training.car_rental_management.enums.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByBookingId(Integer bookingId);
    List<Review> findByAuthorId(Integer authorId);
    List<Review> findByTargetUserId(Integer targetUserId);
    List<Review> findByStatus(ReviewStatus status);
}

