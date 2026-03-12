package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.Booking;
import fa.training.car_rental_management.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByVehicleId(Integer vehicleId);
    List<Booking> findByCustomerId(Integer customerId);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByVehicleIdAndCustomerId(Integer vehicleId, Integer customerId);
}

