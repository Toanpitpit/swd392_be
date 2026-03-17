package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.Payment;
import fa.training.car_rental_management.enums.PaymentStatus;
import fa.training.car_rental_management.enums.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByBookingId(Integer bookingId);
    List<Payment> findByPayerId(Integer payerId);
    List<Payment> findByStatus(PaymentStatus status);
    Payment findByBookingIdAndTypeAndStatus(
            Integer bookingId,
            PaymentType type,
            PaymentStatus status
    );
}

