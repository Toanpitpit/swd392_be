package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {
    List<Availability> findByVehicleId(Integer vehicleId);
    List<Availability> findByVehicleIdAndStartDateBeforeAndEndDateAfter(Integer vehicleId, LocalDateTime endDate, LocalDateTime startDate);
}

