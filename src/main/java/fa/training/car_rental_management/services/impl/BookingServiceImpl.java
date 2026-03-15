package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.dto.request.BookingRequestDTO;
import fa.training.car_rental_management.entities.Booking;
import fa.training.car_rental_management.entities.Vehicle;
import fa.training.car_rental_management.entities.users;
import fa.training.car_rental_management.enums.BookingStatus;
import fa.training.car_rental_management.repository.BookingRepository;
import fa.training.car_rental_management.services.BookingService;
import fa.training.car_rental_management.util.BookingValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingValidator bookingValidator;

    @Override
    public Booking createBooking(BookingRequestDTO booking) {
        try {
            log.info("Creating booking for vehicle: {} by customer: {}", booking.getVehicleId(), booking.getCustomerId());

            Vehicle vehicle = bookingValidator.validateVehicleExists(booking.getVehicleId());

            users customer = bookingValidator.validateCustomerExists(booking.getCustomerId());

            bookingValidator.validateBookingTimes(booking.getStartTime(), booking.getEndTime());

            bookingValidator.checkAndUpdateAvailability(vehicle.getId(), booking.getStartTime(), booking.getEndTime());

            Booking savedBooking = createAndSaveBooking(booking);
            
            log.info("Booking created successfully with ID: {}", savedBooking.getId());
            return savedBooking;

        } catch (Exception e) {
            log.error("Error creating booking: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Create and save new booking
     */
    private Booking createAndSaveBooking(BookingRequestDTO bookingDTO) {
        Booking booking = new Booking();
        booking.setCustomerId(bookingDTO.getCustomerId());
        booking.setVehicleId(bookingDTO.getVehicleId());
        booking.setStartTime(bookingDTO.getStartTime());
        booking.setEndTime(bookingDTO.getEndTime());
        booking.setStatus(BookingStatus.PENDING_APPROVAL);
        booking.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking saved - ID: {}, Vehicle: {}, Customer: {}", 
                 savedBooking.getId(), savedBooking.getVehicleId(), savedBooking.getCustomerId());
        
        return savedBooking;
    }

    @Override
    public Optional<Booking> getBookingById(Integer id) {
        try {
            log.info("Fetching booking with id: {}", id);
            return bookingRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Booking> getBookingsByVehicleId(Integer vehicleId) {
        try {
            log.info("Fetching bookings for vehicle: {}", vehicleId);
            return bookingRepository.findByVehicleId(vehicleId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Booking> getBookingsByCustomerId(Integer customerId) {
        try {
            log.info("Fetching bookings for customer: {}", customerId);
            return bookingRepository.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        log.info("Fetching bookings with status: {}", status);
        return bookingRepository.findByStatus(status);
    }

    @Override
    public List<Booking> getBookingsByVehicleIdAndCustomerId(Integer vehicleId, Integer customerId) {
        log.info("Fetching bookings for vehicle: {} and customer: {}", vehicleId, customerId);
        return bookingRepository.findByVehicleIdAndCustomerId(vehicleId, customerId);
    }

    @Override
    public List<Booking> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingRepository.findAll();
    }

    @Override
    public Booking updateBooking(Booking booking) {
        log.info("Updating booking with id: {}", booking.getId());
        return bookingRepository.save(booking);
    }

    @Override
    public void deleteBooking(Integer id) {
        log.info("Deleting booking with id: {}", id);
        bookingRepository.deleteById(id);
    }
}

