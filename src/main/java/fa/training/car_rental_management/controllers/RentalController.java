package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.dto.BookingPickupRequest;
import fa.training.car_rental_management.dto.BookingPickupResponse;
import fa.training.car_rental_management.entities.*;
import fa.training.car_rental_management.enums.BookingStatus;
import fa.training.car_rental_management.enums.CarStatus;
import fa.training.car_rental_management.enums.InspectionType;
import fa.training.car_rental_management.enums.PaymentType;
import fa.training.car_rental_management.enums.PaymentStatus;
import fa.training.car_rental_management.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/rentals")
@CrossOrigin(origins = "*")
public class RentalController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private UserService userService;

    /**
     * Complete rental pickup flow
     * POST /api/rentals/pickup
     * 
     * Workflow:
     * 1. Create booking (PENDING_APPROVAL)
     * 2. Approve booking
     * 3. Process payment (security deposit)
     * 4. Create pickup inspection
     * 5. Activate booking (ACTIVE)
     */
//    @PostMapping("/pickup")
//    public ResponseEntity<ApiResponse<BookingPickupResponse>> completeRentalPickup(
//            @RequestBody BookingPickupRequest request) {
//        try {
//            log.info("Starting rental pickup flow for vehicle: {} by customer: {}",
//                    request.getVehicleId(), request.getCustomerId());
//
//            // Step 1: Create Booking
//            Booking booking = Booking.builder()
//                    .vehicleId(request.getVehicleId())
//                    .customerId(request.getCustomerId())
//                    .startTime(request.getStartTime())
//                    .endTime(request.getEndTime())
//                    .status(BookingStatus.PENDING_APPROVAL)
//                    .build();
//
//            Booking createdBooking = bookingService.createBooking(booking);
//            log.info("Booking created with id: {}", createdBooking.getId());
//
//            // Step 2: Approve Booking
//            createdBooking.setStatus(BookingStatus.APPROVED);
//            Booking approvedBooking = bookingService.updateBooking(createdBooking);
//            log.info("Booking approved");
//
//            // Step 3: Process Payment (Security Deposit)
//            Payment payment = Payment.builder()
//                    .bookingId(approvedBooking.getId())
//                    .payerId(request.getCustomerId())
//                    .amount(request.getSecurityDepositAmount())
//                    .type(PaymentType.SECURITY_DEPOSIT)
//                    .status(PaymentStatus.COMPLETED)
//                    .build();
//
//            Payment createdPayment = paymentService.createPayment(payment);
//            log.info("Security deposit payment created");
//
//            // Step 4: Create Pickup Inspection
//            Inspection inspection = Inspection.builder()
//                    .bookingId(approvedBooking.getId())
//                    .inspectorId(request.getInspectorId())
//                    .type(InspectionType.PICKUP)
//                    .carStatus(request.getCarStatus())
//                    .comments(request.getInspectionComments())
//                    .date(LocalDateTime.now())
//                    .build();
//
//            Inspection pickupInspection = inspectionService.createInspection(inspection);
//            log.info("Pickup inspection created");
//
//            // Step 5: Activate Booking (Mark as ACTIVE)
//            approvedBooking.setStatus(BookingStatus.ACTIVE);
//            Booking activeBooking = bookingService.updateBooking(approvedBooking);
//            log.info("Booking activated - Rental started");
//
//            // Build response
//            BookingPickupResponse response = BookingPickupResponse.builder()
//                    .bookingId(activeBooking.getId())
//                    .bookingStatus(activeBooking.getStatus().toString())
//                    .vehicleId(activeBooking.getVehicleId())
//                    .customerId(activeBooking.getCustomerId())
//                    .startTime(activeBooking.getStartTime())
//                    .endTime(activeBooking.getEndTime())
//                    .paymentId(createdPayment.getId())
//                    .paymentAmount(createdPayment.getAmount())
//                    .paymentStatus(createdPayment.getStatus().toString())
//                    .inspectionId(pickupInspection.getId())
//                    .inspectionType(pickupInspection.getType().toString())
//                    .carStatus(pickupInspection.getCarStatus().toString())
//                    .message("Rental pickup completed successfully")
//                    .build();
//
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body(ApiResponse.success("Rental pickup process completed", response));
//
//        } catch (Exception e) {
//            log.error("Error completing rental pickup", e);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(ApiResponse.error("Failed to complete rental pickup: " + e.getMessage()));
//        }
//    }

    /**
     * Get booking status
     * GET /api/rentals/booking/{bookingId}/status
     */
    @GetMapping("/booking/{bookingId}/status")
    public ResponseEntity<ApiResponse<String>> getBookingStatus(@PathVariable Integer bookingId) {
        try {
            log.info("Fetching booking status for: {}", bookingId);

            Optional<Booking> booking = bookingService.getBookingById(bookingId);

            if (booking.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(
                        "Booking status retrieved",
                        booking.get().getStatus().toString()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Booking not found"));
            }
        } catch (Exception e) {
            log.error("Error fetching booking status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching booking status"));
        }
    }

    /**
     * Get rental details
     * GET /api/rentals/{bookingId}
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingPickupResponse>> getRentalDetails(@PathVariable Integer bookingId) {
        try {
            log.info("Fetching rental details for booking: {}", bookingId);

            Optional<Booking> booking = bookingService.getBookingById(bookingId);

            if (!booking.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Booking not found"));
            }

            Booking bookingData = booking.get();

            // Get payment details
            Optional<Payment> payment = paymentService.getPaymentsByBookingId(bookingId)
                    .stream()
                    .filter(p -> p.getType() == PaymentType.SECURITY_DEPOSIT)
                    .findFirst();

            // Get inspection details
            Optional<Inspection> inspection = inspectionService.getInspectionsByBookingId(bookingId)
                    .stream()
                    .filter(i -> i.getType() == InspectionType.PICKUP)
                    .findFirst();

            BookingPickupResponse response = BookingPickupResponse.builder()
                    .bookingId(bookingData.getId())
                    .bookingStatus(bookingData.getStatus().toString())
                    .vehicleId(bookingData.getVehicleId())
                    .customerId(bookingData.getCustomerId())
                    .startTime(bookingData.getStartTime())
                    .endTime(bookingData.getEndTime())
                    .paymentId(payment.isPresent() ? payment.get().getId() : null)
                    .paymentAmount(payment.isPresent() ? payment.get().getAmount() : null)
                    .paymentStatus(payment.isPresent() ? payment.get().getStatus().toString() : null)
                    .inspectionId(inspection.isPresent() ? inspection.get().getId() : null)
                    .inspectionType(inspection.isPresent() ? inspection.get().getType().toString() : null)
                    .carStatus(inspection.isPresent() ? inspection.get().getCarStatus().toString() : null)
                    .message("Rental details retrieved successfully")
                    .build();

            return ResponseEntity.ok(ApiResponse.success("Rental details retrieved", response));

        } catch (Exception e) {
            log.error("Error fetching rental details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching rental details"));
        }
    }

    /**
     * Start rental (mark booking as ACTIVE)
     * PATCH /api/rentals/{bookingId}/start
     */
    @PatchMapping("/{bookingId}/start")
    public ResponseEntity<ApiResponse<Booking>> startRental(@PathVariable Integer bookingId) {
        try {
            log.info("Starting rental for booking: {}", bookingId);

            Optional<Booking> booking = bookingService.getBookingById(bookingId);

            if (booking.isPresent()) {
                Booking bookingData = booking.get();
                bookingData.setStatus(BookingStatus.ACTIVE);
                Booking updatedBooking = bookingService.updateBooking(bookingData);

                return ResponseEntity.ok(ApiResponse.success(
                        "Rental started successfully", updatedBooking));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Booking not found"));
            }
        } catch (Exception e) {
            log.error("Error starting rental", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to start rental: " + e.getMessage()));
        }
    }

    /**
     * Complete rental (mark booking as UNDER_INSPECTION)
     * PATCH /api/rentals/{bookingId}/complete
     */
    @PatchMapping("/{bookingId}/complete")
    public ResponseEntity<ApiResponse<Booking>> completeRental(@PathVariable Integer bookingId) {
        try {
            log.info("Completing rental for booking: {}", bookingId);

            Optional<Booking> booking = bookingService.getBookingById(bookingId);

            if (booking.isPresent()) {
                Booking bookingData = booking.get();
                bookingData.setStatus(BookingStatus.UNDER_INSPECTION);
                Booking updatedBooking = bookingService.updateBooking(bookingData);

                return ResponseEntity.ok(ApiResponse.success(
                        "Rental marked for inspection", updatedBooking));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Booking not found"));
            }
        } catch (Exception e) {
            log.error("Error completing rental", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to complete rental: " + e.getMessage()));
        }
    }

    /**
     * Finish rental (mark booking as COMPLETED)
     * PATCH /api/rentals/{bookingId}/finish
     */
    @PatchMapping("/{bookingId}/finish")
    public ResponseEntity<ApiResponse<Booking>> finishRental(@PathVariable Integer bookingId) {
        try {
            log.info("Finishing rental for booking: {}", bookingId);

            Optional<Booking> booking = bookingService.getBookingById(bookingId);

            if (booking.isPresent()) {
                Booking bookingData = booking.get();
                bookingData.setStatus(BookingStatus.COMPLETED);
                Booking updatedBooking = bookingService.updateBooking(bookingData);

                return ResponseEntity.ok(ApiResponse.success(
                        "Rental finished successfully", updatedBooking));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Booking not found"));
            }
        } catch (Exception e) {
            log.error("Error finishing rental", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to finish rental: " + e.getMessage()));
        }
    }

    /**
     * Cancel rental
     * PATCH /api/rentals/{bookingId}/cancel
     */
    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<Booking>> cancelRental(
            @PathVariable Integer bookingId,
            @RequestParam String reason) {
        try {
            log.info("Cancelling rental for booking: {}", bookingId);

            Optional<Booking> booking = bookingService.getBookingById(bookingId);

            if (booking.isPresent()) {
                Booking bookingData = booking.get();
                bookingData.setStatus(BookingStatus.CANCELLED);
                bookingData.setRejectionReason(reason);
                Booking updatedBooking = bookingService.updateBooking(bookingData);

                return ResponseEntity.ok(ApiResponse.success(
                        "Rental cancelled successfully", updatedBooking));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Booking not found"));
            }
        } catch (Exception e) {
            log.error("Error cancelling rental", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to cancel rental: " + e.getMessage()));
        }
    }
}

