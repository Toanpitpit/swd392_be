package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.Availability;
import fa.training.car_rental_management.repository.AvailabilityRepository;
import fa.training.car_rental_management.services.AvailabilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class AvailabilityServiceImpl implements AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Override
    public Availability createAvailability(Availability availability) {
        log.info("Creating availability for vehicle: {}", availability.getVehicleId());
        return availabilityRepository.save(availability);
    }

    @Override
    public Optional<Availability> getAvailabilityById(Integer id) {
        log.info("Fetching availability with id: {}", id);
        return availabilityRepository.findById(id);
    }

    @Override
    public List<Availability> getAvailabilitiesByVehicleId(Integer vehicleId) {
        log.info("Fetching availabilities for vehicle: {}", vehicleId);
        return availabilityRepository.findByVehicleId(vehicleId);
    }

    @Override
    public List<Availability> getAvailabilitiesByVehicleIdAndDateRange(Integer vehicleId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching availabilities for vehicle: {} between {} and {}", vehicleId, startDate, endDate);
        return availabilityRepository.findByVehicleIdAndStartDateBeforeAndEndDateAfter(vehicleId, endDate, startDate);
    }

    @Override
    public List<Availability> getAllAvailabilities() {
        log.info("Fetching all availabilities");
        return availabilityRepository.findAll();
    }

    @Override
    public Availability updateAvailability(Availability availability) {
        log.info("Updating availability with id: {}", availability.getId());
        return availabilityRepository.save(availability);
    }

    @Override
    public void deleteAvailability(Integer id) {
        log.info("Deleting availability with id: {}", id);
        availabilityRepository.deleteById(id);
    }
}

