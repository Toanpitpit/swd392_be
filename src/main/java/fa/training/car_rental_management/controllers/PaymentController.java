package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.entities.Payment;
import fa.training.car_rental_management.dto.response.PaymentResponse;
import fa.training.car_rental_management.services.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Payment>>> getAllPayments() {
        try {
            List<Payment> payments = paymentService.getAllPayments();
            log.info(payments.toString());
            return ResponseEntity.ok(new ApiResponse<>(true, "Payments retrieved successfully", payments));
        } catch (Exception e) {
            log.error("Error retrieving payments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to retrieve payments", null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentById(@PathVariable Integer id) {
        try {
            List<Payment> payments = paymentService.getPaymentsByBookingId(id);
            if (!payments.isEmpty()) {
                List<PaymentResponse> response = payments.stream()
                        .map(p -> PaymentResponse.builder()
                                .id(p.getId())
                                .bookingId(p.getBookingId())
                                .payerId(p.getPayerId())
                                .amount(BigDecimal.valueOf(p.getAmount()))
                                .paymentType(p.getType() != null ? p.getType().name() : null)
                                .status(p.getStatus() != null ? p.getStatus().name() : null)
                                .build())
                        .toList();
                return ResponseEntity.ok(ApiResponse.success("Payment retrieved successfully", response));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Payment not found for booking ID: " + id));
            }
        } catch (Exception e) {
            log.error("Error retrieving payment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Failed to retrieve payment"));
        }
    }
}

