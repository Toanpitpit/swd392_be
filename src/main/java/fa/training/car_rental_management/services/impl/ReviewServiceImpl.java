package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.Review;
import fa.training.car_rental_management.enums.ReviewStatus;
import fa.training.car_rental_management.repository.ReviewRepository;
import fa.training.car_rental_management.services.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public Review createReview(Review review) {
        log.info("Creating review for booking: {} by author: {}", review.getBookingId(), review.getAuthorId());
        return reviewRepository.save(review);
    }

    @Override
    public Optional<Review> getReviewById(Integer id) {
        log.info("Fetching review with id: {}", id);
        return reviewRepository.findById(id);
    }

    @Override
    public List<Review> getReviewsByBookingId(Integer bookingId) {
        log.info("Fetching reviews for booking: {}", bookingId);
        return reviewRepository.findByBookingId(bookingId);
    }

    @Override
    public List<Review> getReviewsByAuthorId(Integer authorId) {
        log.info("Fetching reviews by author: {}", authorId);
        return reviewRepository.findByAuthorId(authorId);
    }

    @Override
    public List<Review> getReviewsByTargetUserId(Integer targetUserId) {
        log.info("Fetching reviews for target user: {}", targetUserId);
        return reviewRepository.findByTargetUserId(targetUserId);
    }

    @Override
    public List<Review> getReviewsByStatus(ReviewStatus status) {
        log.info("Fetching reviews with status: {}", status);
        return reviewRepository.findByStatus(status);
    }

    @Override
    public List<Review> getAllReviews() {
        log.info("Fetching all reviews");
        return reviewRepository.findAll();
    }

    @Override
    public Review updateReview(Review review) {
        log.info("Updating review with id: {}", review.getId());
        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Integer id) {
        log.info("Deleting review with id: {}", id);
        reviewRepository.deleteById(id);
    }
}

