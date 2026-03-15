package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.Availability;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AvailabilityService {
    Availability createAvailability(Availability availability);
    Optional<Availability> getAvailabilityById(Integer id);
    List<Availability> getAvailabilitiesByVehicleId(Integer vehicleId);
    List<Availability> getAvailabilitiesByVehicleIdAndDateRange(Integer vehicleId, LocalDateTime startDate, LocalDateTime endDate);
    List<Availability> getAllAvailabilities();
    Availability updateAvailability(Availability availability);
    void deleteAvailability(Integer id);
}

