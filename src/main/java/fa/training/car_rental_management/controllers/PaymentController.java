package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.entities.Payment;
import fa.training.car_rental_management.enums.PaymentStatus;
import fa.training.car_rental_management.enums.PaymentType;
import fa.training.car_rental_management.services.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Create payment for booking
     * POST /api/payments
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Payment>> createPayment(@RequestBody Payment payment) {
        try {
            log.info("Creating payment for booking: {} by payer: {}", 
                    payment.getBookingId(), payment.getPayerId());
            
            Payment createdPayment = paymentService.createPayment(payment);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Payment created successfully", createdPayment));
        } catch (Exception e) {
            log.error("Error creating payment", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create payment: " + e.getMessage()));
        }
    }

    /**
     * Get payment by ID
     * GET /api/payments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Payment>> getPaymentById(@PathVariable("id") Integer id) {
        try {
            log.info("Fetching payment with id: {}", id);
            
            Optional<Payment> payment = paymentService.getPaymentById(id);
            
            if (payment.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Payment retrieved successfully", payment.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Payment not found with id: " + id));
            }
        } catch (Exception e) {
            log.error("Error fetching payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching payment"));
        }
    }

    /**
     * Get payments by booking ID
     * GET /api/payments/booking/{bookingId}
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentsByBookingId(@PathVariable("bookingId") Integer bookingId) {
        try {
            log.info("Fetching payments for booking: {}", bookingId);
            
            List<Payment> payments = paymentService.getPaymentsByBookingId(bookingId);
            
            return ResponseEntity.ok(ApiResponse.success(
                    "Payments retrieved successfully", payments));
        } catch (Exception e) {
            log.error("Error fetching payments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching payments"));
        }
    }

    /**
     * Get payments by payer ID
     * GET /api/payments/payer/{payerId}
     */
    @GetMapping("/payer/{payerId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentsByPayerId(@PathVariable("payerId") Integer payerId) {
        try {
            log.info("Fetching payments by payer: {}", payerId);
            
            List<Payment> payments = paymentService.getPaymentsByPayerId(payerId);
            
            return ResponseEntity.ok(ApiResponse.success(
                    "Payments retrieved successfully", payments));
        } catch (Exception e) {
            log.error("Error fetching payments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching payments"));
        }
    }

    /**
     * Get payments by status
     * GET /api/payments/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentsByStatus(@PathVariable("status") PaymentStatus status) {
        try {
            log.info("Fetching payments with status: {}", status);
            
            List<Payment> payments = paymentService.getPaymentsByStatus(status);
            
            return ResponseEntity.ok(ApiResponse.success(
                    "Payments retrieved successfully", payments));
        } catch (Exception e) {
            log.error("Error fetching payments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching payments"));
        }
    }

    /**
     * Get all payments
     * GET /api/payments
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Payment>>> getAllPayments() {
        try {
            log.info("Fetching all payments");
            
            List<Payment> payments = paymentService.getAllPayments();
            
            return ResponseEntity.ok(ApiResponse.success(
                    "All payments retrieved successfully", payments));
        } catch (Exception e) {
            log.error("Error fetching payments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching payments"));
        }
    }

    /**
     * Process payment for security deposit
     * POST /api/payments/deposit
     */
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<Payment>> processSecurityDeposit(
            @RequestParam("bookingId") Integer bookingId,
            @RequestParam("payerId") Integer payerId,
            @RequestParam("amount") Double amount) {
        try {
            log.info("Processing security deposit for booking: {}", bookingId);
            
            Payment payment = Payment.builder()
                    .bookingId(bookingId)
                    .payerId(payerId)
                    .amount(amount)
                    .type(PaymentType.SECURITY_DEPOSIT)
                    .status(PaymentStatus.PENDING)
                    .build();
            
            Payment createdPayment = paymentService.createPayment(payment);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Security deposit processed", createdPayment));
        } catch (Exception e) {
            log.error("Error processing security deposit", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to process security deposit: " + e.getMessage()));
        }
    }

    /**
     * Mark payment as completed
     * PATCH /api/payments/{id}/complete
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<Payment>> completePayment(@PathVariable("id") Integer id) {
        try {
            log.info("Marking payment as completed: {}", id);
            
            Optional<Payment> existingPayment = paymentService.getPaymentById(id);
            
            if (existingPayment.isPresent()) {
                Payment payment = existingPayment.get();
                payment.setStatus(PaymentStatus.COMPLETED);
                Payment updatedPayment = paymentService.updatePayment(payment);
                
                return ResponseEntity.ok(ApiResponse.success(
                        "Payment completed successfully", updatedPayment));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Payment not found with id: " + id));
            }
        } catch (Exception e) {
            log.error("Error completing payment", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to complete payment: " + e.getMessage()));
        }
    }

    /**
     * Mark payment as failed
     * PATCH /api/payments/{id}/fail
     */
    @PatchMapping("/{id}/fail")
    public ResponseEntity<ApiResponse<Payment>> failPayment(@PathVariable("id") Integer id) {
        try {
            log.info("Marking payment as failed: {}", id);
            
            Optional<Payment> existingPayment = paymentService.getPaymentById(id);
            
            if (existingPayment.isPresent()) {
                Payment payment = existingPayment.get();
                payment.setStatus(PaymentStatus.FAILED);
                Payment updatedPayment = paymentService.updatePayment(payment);
                
                return ResponseEntity.ok(ApiResponse.success(
                        "Payment marked as failed", updatedPayment));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Payment not found with id: " + id));
            }
        } catch (Exception e) {
            log.error("Error failing payment", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to mark payment as failed: " + e.getMessage()));
        }
    }

    /**
     * Update payment
     * PUT /api/payments/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Payment>> updatePayment(
            @PathVariable("id") Integer id,
            @RequestBody Payment payment) {
        try {
            log.info("Updating payment with id: {}", id);
            
            Optional<Payment> existingPayment = paymentService.getPaymentById(id);
            
            if (existingPayment.isPresent()) {
                payment.setId(id);
                Payment updatedPayment = paymentService.updatePayment(payment);
                
                return ResponseEntity.ok(ApiResponse.success(
                        "Payment updated successfully", updatedPayment));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Payment not found with id: " + id));
            }
        } catch (Exception e) {
            log.error("Error updating payment", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update payment: " + e.getMessage()));
        }
    }

    /**
     * Delete payment
     * DELETE /api/payments/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable("id") Integer id) {
        try {
            log.info("Deleting payment with id: {}", id);
            
            Optional<Payment> existingPayment = paymentService.getPaymentById(id);
            
            if (existingPayment.isPresent()) {
                paymentService.deletePayment(id);
                return ResponseEntity.ok(ApiResponse.success("Payment deleted successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Payment not found with id: " + id));
            }
        } catch (Exception e) {
            log.error("Error deleting payment", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete payment: " + e.getMessage()));
        }
    }
}

