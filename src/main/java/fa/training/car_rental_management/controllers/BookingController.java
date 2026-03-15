package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.dto.request.BookingRequestDTO;
import fa.training.car_rental_management.entities.Booking;
import fa.training.car_rental_management.enums.BookingStatus;
import fa.training.car_rental_management.services.impl.BookingServiceImpl;
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
     * Get all bookings for a vehicle
     * GET /api/bookings/vehicle/{vehicleId}
     */
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<List<Booking>>> getBookingsByVehicleId(@PathVariable Integer vehicleId) {
        try {
            log.info("Fetching bookings for vehicle: {}", vehicleId);
            
            List<Booking> bookings = bookingService.getBookingsByVehicleId(vehicleId);
            
            return ResponseEntity.ok(ApiResponse.success(
                    "Bookings retrieved successfully", bookings));
        } catch (Exception e) {
            log.error("Error fetching bookings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching bookings"));
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


    /**
     * Update booking status (PENDING_APPROVAL -> AWAITING_PAYMENT -> APPROVED -> ACTIVE)
     * PUT /api/bookings/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Booking>> updateBooking(
            @PathVariable Integer id, 
            @RequestBody Booking booking) {
        try {
            log.info("Updating booking with id: {}", id);
            
            Optional<Booking> existingBooking = bookingService.getBookingById(id);
            
            if (existingBooking.isPresent()) {
                booking.setId(id);
                Booking updatedBooking = bookingService.updateBooking(booking);
                
                return ResponseEntity.ok(ApiResponse.success(
                        "Booking updated successfully", updatedBooking));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Booking not found with id: " + id));
            }
        } catch (Exception e) {
            log.error("Error updating booking", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update booking: " + e.getMessage()));
        }
    }

    /**
     * Update booking status
     * PATCH /bookings/{id}/status
     * Requires: CAR_OWNER or ADMIN role
     */
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

    /**
     * Reject booking with reason
     * PATCH /bookings/{id}/reject
     * Requires: CAR_OWNER or ADMIN role
     */
    @PreAuthorize("hasAuthority('CAR_OWNER') or hasAuthority('ADMIN')")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Booking>> rejectBooking(
            @PathVariable Integer id,
            @RequestParam String reason) {
        try {
            log.info("Rejecting booking id: {} with reason: {}", id, reason);
            
            Optional<Booking> existingBooking = bookingService.getBookingById(id);
            
            if (existingBooking.isPresent()) {
                Booking booking = existingBooking.get();
                booking.setStatus(BookingStatus.REJECTED);
                booking.setRejectionReason(reason);
                Booking updatedBooking = bookingService.updateBooking(booking);
                
                return ResponseEntity.ok(ApiResponse.success(
                        "Booking rejected successfully", updatedBooking));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Booking not found with id: " + id));
            }
        } catch (Exception e) {
            log.error("Error rejecting booking", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to reject booking: " + e.getMessage()));
        }
    }


    /**
     * Approve booking
     * PATCH /bookings/{id}/approve
     * Requires: CAR_OWNER or ADMIN role
     */
    @PreAuthorize("hasAuthority('CAR_OWNER') or hasAuthority('ADMIN')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Booking>> approveBooking(@PathVariable Integer id) {
        try {
            log.info("Approving booking id: {}", id);
            
            Optional<Booking> existingBooking = bookingService.getBookingById(id);
            
            if (existingBooking.isPresent()) {
                Booking booking = existingBooking.get();
                booking.setStatus(BookingStatus.APPROVED);
                Booking updatedBooking = bookingService.updateBooking(booking);
                
                return ResponseEntity.ok(ApiResponse.success(
                        "Booking approved successfully", updatedBooking));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Booking not found with id: " + id));
            }
        } catch (Exception e) {
            log.error("Error approving booking", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to approve booking: " + e.getMessage()));
        }
    }

    /**
     * Mark booking as active (rental started)
     * PATCH /api/bookings/{id}/activate
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Booking>> activateBooking(@PathVariable Integer id) {
        try {
            log.info("Activating booking id: {}", id);
            
            Optional<Booking> existingBooking = bookingService.getBookingById(id);
            
            if (existingBooking.isPresent()) {
                Booking booking = existingBooking.get();
                booking.setStatus(BookingStatus.ACTIVE);
                Booking updatedBooking = bookingService.updateBooking(booking);
                
                return ResponseEntity.ok(ApiResponse.success(
                        "Booking activated successfully (Rental Started)", updatedBooking));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Booking not found with id: " + id));
            }
        } catch (Exception e) {
            log.error("Error activating booking", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to activate booking: " + e.getMessage()));
        }
    }

    /**
     * Delete booking
     * DELETE /api/bookings/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBooking(@PathVariable Integer id) {
        try {
            log.info("Deleting booking with id: {}", id);
            
            Optional<Booking> existingBooking = bookingService.getBookingById(id);
            
            if (existingBooking.isPresent()) {
                bookingService.deleteBooking(id);
                return ResponseEntity.ok(ApiResponse.success("Booking deleted successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Booking not found with id: " + id));
            }
        } catch (Exception e) {
            log.error("Error deleting booking", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete booking: " + e.getMessage()));
        }
    }
}

