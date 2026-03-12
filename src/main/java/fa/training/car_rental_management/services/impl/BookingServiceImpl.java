package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.Booking;
import fa.training.car_rental_management.enums.BookingStatus;
import fa.training.car_rental_management.repository.BookingRepository;
import fa.training.car_rental_management.services.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public Booking createBooking(Booking booking) {
        log.info("Creating booking for vehicle: {} by customer: {}", booking.getVehicleId(), booking.getCustomerId());
        return bookingRepository.save(booking);
    }

    @Override
    public Optional<Booking> getBookingById(Integer id) {
        log.info("Fetching booking with id: {}", id);
        return bookingRepository.findById(id);
    }

    @Override
    public List<Booking> getBookingsByVehicleId(Integer vehicleId) {
        log.info("Fetching bookings for vehicle: {}", vehicleId);
        return bookingRepository.findByVehicleId(vehicleId);
    }

    @Override
    public List<Booking> getBookingsByCustomerId(Integer customerId) {
        log.info("Fetching bookings for customer: {}", customerId);
        return bookingRepository.findByCustomerId(customerId);
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

