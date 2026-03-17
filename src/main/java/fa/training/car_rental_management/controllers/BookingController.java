package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.dto.request.BookingRequestDTO;
import fa.training.car_rental_management.entities.Booking;
import fa.training.car_rental_management.enums.BookingStatus;
import fa.training.car_rental_management.services.impl.BookingServiceImpl;
import fa.training.car_rental_management.util.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingServiceImpl bookingService;
    @Autowired
    private JwtService jwtService;

    /**
     * Create a new booking
     * POST /bookings
     * Requires: CUSTOMER role
     */
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PostMapping
    public ResponseEntity<ApiResponse<Booking>> createBooking(@RequestBody BookingRequestDTO booking) {
        try {
            log.info("Creating new booking for vehicle: {} by customer: {}", 
                    booking.getVehicleId(), booking.getCustomerId());

            Booking createdBooking = bookingService.createBooking(booking);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Booking created successfully", createdBooking));
        } catch (Exception e) {
            log.error("Error creating booking", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create booking: " + e.getMessage()));
        }
    }

    /**
     * Get booking by ID
     * GET /api/bookings/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Booking>> getBookingById(@PathVariable Integer id) {
        try {
            log.info("Fetching booking with id: {}", id);
            
            Optional<Booking> booking = bookingService.getBookingById(id);
            
            if (booking.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Booking retrieved successfully", booking.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Booking not found with id: " + id));
            }
        } catch (Exception e) {
            log.error("Error fetching booking", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching booking"));
        }
    }

    /**
     * Get all bookings for a customer
     * GET /api/bookings/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<Booking>>> getBookingsByCustomerId(@PathVariable Integer customerId) {
        try {
            log.info("Fetching bookings for customer: {}", customerId);
            
            List<Booking> bookings = bookingService.getBookingsByCustomerId(customerId);
            
            return ResponseEntity.ok(ApiResponse.success(
                    "Bookings retrieved successfully", bookings));
        } catch (Exception e) {
            log.error("Error fetching bookings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching bookings"));
        }
    }

    /**
     * Get bookings by status
     * GET /api/bookings/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Booking>>> getBookingsByStatus(@PathVariable BookingStatus status) {
        try {
            log.info("Fetching bookings with status: {}", status);
            
            List<Booking> bookings = bookingService.getBookingsByStatus(status);
            
            return ResponseEntity.ok(ApiResponse.success(
                    "Bookings retrieved successfully", bookings));
        } catch (Exception e) {
            log.error("Error fetching bookings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching bookings"));
        }
    }


    /**
     * Get all bookings
     * GET /api/bookings
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Booking>>> getAllBookings() {
        try {
            log.info("Fetching all bookings");
            
            List<Booking> bookings = bookingService.getAllBookings();
            
            return ResponseEntity.ok(ApiResponse.success(
                    "All bookings retrieved successfully", bookings));
        } catch (Exception e) {
            log.error("Error fetching bookings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching bookings"));
        }
    }


    @PreAuthorize("hasAuthority('CAR_OWNER') or hasAuthority('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Booking>> updateBookingStatus(
            @PathVariable Integer id,
            @RequestParam BookingStatus status) {
        try {
            log.info("Updating booking status to: {} for booking id: {}", status, id);
            
            Optional<Booking> existingBooking = bookingService.getBookingById(id);
            
            if (existingBooking.isPresent()) {
                Booking booking = existingBooking.get();
                booking.setStatus(status);
                Booking updatedBooking = bookingService.updateBooking(booking);
                
                return ResponseEntity.ok(ApiResponse.success(
                        "Booking status updated successfully", updatedBooking));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Booking not found with id: " + id));
            }
        } catch (Exception e) {
            log.error("Error updating booking status", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update booking status: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasAuthority('CAR_OWNER')")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Booking>> rejectBooking(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String reason) {
        try {
            log.info("Rejecting booking id: {} with reason: {}", id, reason);
            String token = authHeader.replace("Bearer ", "").trim();
            Optional<Integer> onwerId = jwtService.extractId(token);
            bookingService.rejectBooking(id, reason, onwerId.get());
            return ResponseEntity.ok(ApiResponse.success("successfully reject booking", null));

        } catch (Exception e) {
            log.error("Error rejecting booking", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to reject booking: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasAuthority('CAR_OWNER')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Booking>> approveBooking(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id) {
        try {
            log.info("Approving booking id: {}", id);
            String token = authHeader.replace("Bearer ", "").trim();
            Optional<Integer> onwerId = jwtService.extractId(token);
            bookingService.approveBooking(id, onwerId.get());
            return ResponseEntity.ok(ApiResponse.success("successfully approved booking", null));
        } catch (Exception e) {
            log.error("Error approving booking", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to approve booking: " + e.getMessage()));
        }
    }

}

