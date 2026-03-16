package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.entities.PaymentMethod;
import fa.training.car_rental_management.services.PaymentMethodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/payment-methods")
@CrossOrigin(origins = "*")
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentMethod>> createPaymentMethod(@RequestBody PaymentMethod paymentMethod) {
        try {
            PaymentMethod createdMethod = paymentMethodService.createPaymentMethod(paymentMethod);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Payment method created successfully", createdMethod));
        } catch (Exception e) {
            log.error("Error creating payment method", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create payment method: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentMethod>> getPaymentMethodById(@PathVariable Integer id) {
        try {
            Optional<PaymentMethod> method = paymentMethodService.getPaymentMethodById(id);
            return method.map(m -> ResponseEntity.ok(ApiResponse.success("Payment method retrieved", m)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Payment method not found")));
        } catch (Exception e) {
            log.error("Error retrieving payment method", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving payment method: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PaymentMethod>>> getPaymentMethodsByUserId(@PathVariable Integer userId) {
        try {
            List<PaymentMethod> methods = paymentMethodService.getPaymentMethodsByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("Payment methods retrieved", methods));
        } catch (Exception e) {
            log.error("Error retrieving payment methods", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving payment methods: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentMethod>> updatePaymentMethod(@PathVariable Integer id,
            @RequestBody PaymentMethod paymentMethod) {
        try {
            paymentMethod.setId(id);
            PaymentMethod updatedMethod = paymentMethodService.updatePaymentMethod(paymentMethod);
            return ResponseEntity.ok(ApiResponse.success("Payment method updated successfully", updatedMethod));
        } catch (Exception e) {
            log.error("Error updating payment method", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update payment method: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePaymentMethod(@PathVariable Integer id) {
        try {
            paymentMethodService.deletePaymentMethod(id);
            return ResponseEntity.ok(ApiResponse.success("Payment method deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting payment method", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete payment method: " + e.getMessage()));
        }
    }
}
