package fa.training.car_rental_management.services;


import fa.training.car_rental_management.dto.request.BookingRequestDTO;
import fa.training.car_rental_management.dto.request.ConfirmReturnRequest;
import fa.training.car_rental_management.dto.response.BookingResponse;
import fa.training.car_rental_management.dto.response.WaitingReturnResponse;
import fa.training.car_rental_management.entities.Booking;
import fa.training.car_rental_management.enums.BookingStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public interface BookingService {
    Booking createBooking(BookingRequestDTO booking);
    Optional<Booking> getBookingById(Integer id);
    List<Booking> getBookingsByCustomerId(Integer customerId);
    List<Booking> getBookingsByStatus(BookingStatus status);
    List<Booking> getAllBookings();
    Booking updateBooking(Booking booking);
    List<WaitingReturnResponse> getWaitingReturnConfirm(Integer ownerId);
    void confirmReturn(Integer bookingId, ConfirmReturnRequest request);
    List<BookingResponse> getMyBookings(String token);
}
