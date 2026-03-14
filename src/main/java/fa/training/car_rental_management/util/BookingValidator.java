package fa.training.car_rental_management.util;

import fa.training.car_rental_management.entities.Availability;
import fa.training.car_rental_management.entities.users;
import fa.training.car_rental_management.entities.Vehicle;
import fa.training.car_rental_management.enums.AvailabilityType;
import fa.training.car_rental_management.repository.AvailabilityRepository;
import fa.training.car_rental_management.repository.UserRepository;
import fa.training.car_rental_management.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * BookingValidator Utility Class
 * Provides reusable validation methods for booking and availability operations
 * Can be used by BookingService, PaymentService, InspectionService, etc.
 */
@Slf4j
@Component
public class BookingValidator {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    /**
     * Validate vehicle exists in database
     * 
     * @param vehicleId the vehicle ID to validate
     * @return Vehicle object if found
     * @throws RuntimeException if vehicle not found
     */
    public Vehicle validateVehicleExists(Integer vehicleId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (!vehicleOpt.isPresent()) {
            throw new RuntimeException("Vehicle not found with ID: " + vehicleId);
        }
        log.debug("Vehicle validated: {}", vehicleId);
        return vehicleOpt.get();
    }

    /**
     * Validate customer (user) exists in database
     * 
     * @param customerId the customer ID to validate
     * @return users object if found
     * @throws RuntimeException if customer not found
     */
    public users validateCustomerExists(Integer customerId) {
        Optional<users> customerOpt = userRepository.findById(customerId);
        if (!customerOpt.isPresent()) {
            throw new RuntimeException("Customer not found with ID: " + customerId);
        }
        log.debug("Customer validated: {}", customerId);
        return customerOpt.get();
    }

    /**
     * Validate booking times
     * - Start time must be in future
     * - End time must be after start time
     * 
     * @param startTime the booking start time
     * @param endTime the booking end time
     * @throws RuntimeException if times are invalid
     */
    public void validateBookingTimes(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();

        // Check start time is in future
        if (startTime.isBefore(now)) {
            throw new RuntimeException("Start time must be in the future");
        }

        // Check end time is after start time
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new RuntimeException("End time must be after start time");
        }

        log.debug("Booking times validated - Start: {}, End: {}", startTime, endTime);
    }

    /**
     * Check availability and update/create availability records
     * If vehicle not available (BLOCKED/SOFT_BLOCKED) -> throw exception
     * If no availability found -> create new
     * If found available -> update
     * 
     * @param vehicleId the vehicle ID
     * @param startTime booking start time
     * @param endTime booking end time
     * @throws RuntimeException if vehicle not available
     */
    public void checkAndUpdateAvailability(Integer vehicleId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Availability> availabilities = availabilityRepository.findByVehicleId(vehicleId);

        boolean isAvailable = false;
        Availability availabilityToUpdate = null;

        // Check existing availabilities
        for (Availability availability : availabilities) {
            // Check if booking period overlaps with availability period
            boolean periodOverlaps = !endTime.isBefore(availability.getStartDate()) &&
                    !startTime.isAfter(availability.getEndDate());

            if (periodOverlaps) {
                // Check if availability status allows booking
                if (availability.getType() == AvailabilityType.BLOCKED || 
                    availability.getType() == AvailabilityType.SOFT_BLOCKED) {
                    throw new RuntimeException("Vehicle not available for the requested time period");
                }

                if (availability.getType() == AvailabilityType.AVAILABLE) {
                    isAvailable = true;
                    availabilityToUpdate = availability;
                    break;
                }
            }
        }

        // Create new availability if none found
        if (!isAvailable && availabilityToUpdate == null) {
            createNewAvailability(vehicleId, startTime, endTime);
        } 
        // Update existing availability
        else if (availabilityToUpdate != null) {
            updateExistingAvailability(availabilityToUpdate, startTime, endTime);
        }

        log.debug("Availability checked and processed for vehicle: {}", vehicleId);
    }

    /**
     * Create new availability record
     * 
     * @param vehicleId the vehicle ID
     * @param startTime availability start date
     * @param endTime availability end date
     */
    public void createNewAvailability(Integer vehicleId, LocalDateTime startTime, LocalDateTime endTime) {
        Availability newAvailability = new Availability();
        newAvailability.setVehicleId(vehicleId);
        newAvailability.setStartDate(startTime);
        newAvailability.setEndDate(endTime);
        newAvailability.setType(AvailabilityType.AVAILABLE);
        
        availabilityRepository.save(newAvailability);
        log.info("Created new availability record for vehicle: {} (Period: {} to {})", vehicleId, startTime, endTime);
    }

    /**
     * Update existing availability record
     * 
     * @param availability the availability record to update
     * @param startTime new start date
     * @param endTime new end date
     */
    public void updateExistingAvailability(Availability availability, LocalDateTime startTime, LocalDateTime endTime) {
        availability.setStartDate(startTime);
        availability.setEndDate(endTime);
        
        availabilityRepository.save(availability);
        log.info("Updated availability record - ID: {} (Period: {} to {})", availability.getId(), startTime, endTime);
    }

    /**
     * Check if vehicle is available for given time period
     * 
     * @param vehicleId the vehicle ID
     * @param startTime start time to check
     * @param endTime end time to check
     * @return true if available, false otherwise
     */
    public boolean isVehicleAvailable(Integer vehicleId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            List<Availability> availabilities = availabilityRepository.findByVehicleId(vehicleId);

            for (Availability availability : availabilities) {
                boolean periodOverlaps = !endTime.isBefore(availability.getStartDate()) &&
                        !startTime.isAfter(availability.getEndDate());

                if (periodOverlaps) {
                    if (availability.getType() == AvailabilityType.BLOCKED || 
                        availability.getType() == AvailabilityType.SOFT_BLOCKED) {
                        return false;
                    }
                    if (availability.getType() == AvailabilityType.AVAILABLE) {
                        return true;
                    }
                }
            }
            return true; // No conflict = available
        } catch (Exception e) {
            log.error("Error checking vehicle availability: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get availability status for vehicle in given period
     * 
     * @param vehicleId the vehicle ID
     * @param startTime start time
     * @param endTime end time
     * @return AvailabilityType (AVAILABLE, BLOCKED, SOFT_BLOCKED)
     */
    public AvailabilityType getVehicleAvailabilityStatus(Integer vehicleId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Availability> availabilities = availabilityRepository.findByVehicleId(vehicleId);

        for (Availability availability : availabilities) {
            boolean periodOverlaps = !endTime.isBefore(availability.getStartDate()) &&
                    !startTime.isAfter(availability.getEndDate());

            if (periodOverlaps) {
                return availability.getType();
            }
        }
        
        return AvailabilityType.AVAILABLE; // Default if no record found
    }
}

