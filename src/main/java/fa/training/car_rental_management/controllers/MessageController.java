package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.entities.Message;
import fa.training.car_rental_management.services.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<ApiResponse<Message>> createMessage(@RequestBody Message message) {
        try {
            Message createdMessage = messageService.createMessage(message);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Message created successfully", createdMessage));
        } catch (Exception e) {
            log.error("Error creating message", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create message: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Message>> getMessageById(@PathVariable("id") Integer id) {
        try {
            Optional<Message> message = messageService.getMessageById(id);
            return message.map(msg -> ResponseEntity.ok(ApiResponse.success("Message retrieved", msg)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Message not found")));
        } catch (Exception e) {
            log.error("Error retrieving message", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving message: " + e.getMessage()));
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<List<Message>>> getMessagesByBookingId(@PathVariable("bookingId") Integer bookingId) {
        try {
            List<Message> messages = messageService.getMessagesByBookingId(bookingId);
            return ResponseEntity.ok(ApiResponse.success("Messages retrieved", messages));
        } catch (Exception e) {
            log.error("Error retrieving messages", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving messages: " + e.getMessage()));
        }
    }

    @GetMapping("/sender/{senderId}")
    public ResponseEntity<ApiResponse<List<Message>>> getMessagesBySenderId(@PathVariable("senderId") Integer senderId) {
        try {
            List<Message> messages = messageService.getMessagesBySenderId(senderId);
            return ResponseEntity.ok(ApiResponse.success("Messages retrieved", messages));
        } catch (Exception e) {
            log.error("Error retrieving messages", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving messages: " + e.getMessage()));
        }
    }

    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<ApiResponse<List<Message>>> getMessagesByReceiverId(@PathVariable("receiverId") Integer receiverId) {
        try {
            List<Message> messages = messageService.getMessagesByReceiverId(receiverId);
            return ResponseEntity.ok(ApiResponse.success("Messages retrieved", messages));
        } catch (Exception e) {
            log.error("Error retrieving messages", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving messages: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Message>> updateMessage(@PathVariable("id") Integer id, @RequestBody Message message) {
        try {
            message.setId(id);
            Message updatedMessage = messageService.updateMessage(message);
            return ResponseEntity.ok(ApiResponse.success("Message updated successfully", updatedMessage));
        } catch (Exception e) {
            log.error("Error updating message", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update message: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(@PathVariable("id") Integer id) {
        try {
            messageService.deleteMessage(id);
            return ResponseEntity.ok(ApiResponse.success("Message deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting message", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete message: " + e.getMessage()));
        }
    }
}

