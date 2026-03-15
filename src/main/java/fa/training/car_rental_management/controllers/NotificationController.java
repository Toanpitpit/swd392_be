package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.entities.Notification;
import fa.training.car_rental_management.entities.Users;
import fa.training.car_rental_management.repository.UserRepository;
import fa.training.car_rental_management.services.NotificationService;
import fa.training.car_rental_management.services.impl.UserNotificationEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserNotificationEmailService userNotificationEmailService;

    @Autowired
    private UserRepository userRepository;

    // ==================== Original Notification Methods ====================

    @PostMapping
    public ResponseEntity<ApiResponse<Notification>> createNotification(@RequestBody Notification notification) {
        try {
            Notification createdNotification = notificationService.createNotification(notification);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Notification created successfully", createdNotification));
        } catch (Exception e) {
            log.error("Error creating notification", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create notification: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Notification>> getNotificationById(@PathVariable Integer id) {
        try {
            Optional<Notification> notification = notificationService.getNotificationById(id);
            return notification.map(n -> ResponseEntity.ok(ApiResponse.success("Notification retrieved", n)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Notification not found")));
        } catch (Exception e) {
            log.error("Error retrieving notification", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving notification: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotificationsByUserId(@PathVariable Integer userId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("Notifications retrieved", notifications));
        } catch (Exception e) {
            log.error("Error retrieving notifications", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to retrieve notifications: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Notification>> updateNotification(@PathVariable Integer id, @RequestBody Notification notification) {
        try {
            notification.setId(id);
            Notification updatedNotification = notificationService.updateNotification(notification);
            return ResponseEntity.ok(ApiResponse.success("Notification updated successfully", updatedNotification));
        } catch (Exception e) {
            log.error("Error updating notification", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update notification: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Integer id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok(ApiResponse.success("Notification deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting notification", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete notification: " + e.getMessage()));
        }
    }

    // ==================== Test Email Methods ====================

    @PostMapping("/test/booking-accepted")
    public ResponseEntity<ApiResponse<?>> testBookingAcceptedEmail(
            @RequestParam Integer userId,
            @RequestParam(required = false, defaultValue = "123") Integer bookingId,
            @RequestParam(required = false, defaultValue = "Toyota Camry 2024") String vehicleName,
            @RequestParam(required = false, defaultValue = "20 Mar 2026 10:00 AM") String startTime,
            @RequestParam(required = false, defaultValue = "22 Mar 2026 10:00 AM") String endTime,
            @RequestParam(required = false, defaultValue = "299.99") Double totalPrice) {
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> vars = userNotificationEmailService.buildBookingAcceptedVariables(
                bookingId, vehicleName, startTime, endTime, totalPrice
            );
            userNotificationEmailService.sendEmailWithTemplate(
                user, "Booking Confirmed - " + vehicleName, "booking-notification", vars
            );

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Email sent to " + user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Email sent", response));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/test/booking-rejected")
    public ResponseEntity<ApiResponse<?>> testBookingRejectedEmail(
            @RequestParam Integer userId,
            @RequestParam(required = false, defaultValue = "123") Integer bookingId,
            @RequestParam(required = false, defaultValue = "Toyota Camry 2024") String vehicleName,
            @RequestParam(required = false, defaultValue = "Vehicle not available") String rejectionReason) {
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> vars = userNotificationEmailService.buildBookingRejectedVariables(
                bookingId, vehicleName, rejectionReason
            );
            userNotificationEmailService.sendEmailWithTemplate(
                user, "Booking Rejected - " + vehicleName, "booking-notification", vars
            );

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Email sent to " + user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Email sent", response));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/test/vehicle-return")
    public ResponseEntity<ApiResponse<?>> testVehicleReturnEmail(
            @RequestParam Integer userId,
            @RequestParam(required = false, defaultValue = "123") Integer bookingId,
            @RequestParam(required = false, defaultValue = "Toyota Camry 2024") String vehicleName,
            @RequestParam(required = false, defaultValue = "22 Mar 2026 10:30 AM") String returnDateTime,
            @RequestParam(required = false, defaultValue = "50.00") Double depositAmount,
            @RequestParam(required = false, defaultValue = "Approved") String refundStatus) {
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> vars = userNotificationEmailService.buildVehicleReturnVariables(
                bookingId, vehicleName, returnDateTime, depositAmount, refundStatus
            );
            userNotificationEmailService.sendEmailWithTemplate(
                user, "Vehicle Return - " + vehicleName, "vehicle-return-notification", vars
            );

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Email sent to " + user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Email sent", response));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/test/payment-completed")
    public ResponseEntity<ApiResponse<?>> testPaymentCompletedEmail(
            @RequestParam Integer userId,
            @RequestParam(required = false, defaultValue = "123") Integer bookingId,
            @RequestParam(required = false, defaultValue = "299.99") Double paymentAmount,
            @RequestParam(required = false, defaultValue = "Credit Card") String paymentMethod) {
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> vars = userNotificationEmailService.buildPaymentCompletedVariables(
                bookingId, paymentAmount, paymentMethod
            );
            userNotificationEmailService.sendEmailWithTemplate(
                user, "Payment Received", "payment-notification", vars
            );

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Email sent to " + user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Email sent", response));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/test/inspection-completed")
    public ResponseEntity<ApiResponse<?>> testInspectionCompletedEmail(
            @RequestParam Integer userId,
            @RequestParam(required = false, defaultValue = "123") Integer bookingId,
            @RequestParam(required = false, defaultValue = "Passed") String inspectionStatus,
            @RequestParam(required = false, defaultValue = "All checks passed") String notes) {
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> vars = userNotificationEmailService.buildInspectionCompletedVariables(
                bookingId, inspectionStatus, notes
            );
            userNotificationEmailService.sendEmailWithTemplate(
                user, "Inspection Report - Booking #" + bookingId, "inspection-notification", vars
            );

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Email sent to " + user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Email sent", response));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/test/users")
    public ResponseEntity<ApiResponse<?>> getAllUsers() {
        try {
            var users = userRepository.findAll();
            return ResponseEntity.ok(ApiResponse.success("Users retrieved", users));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }
}

