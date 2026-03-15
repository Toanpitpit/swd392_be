package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.entities.Notification;
import fa.training.car_rental_management.services.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

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
                    .body(ApiResponse.error("Error retrieving notifications: " + e.getMessage()));
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
}

