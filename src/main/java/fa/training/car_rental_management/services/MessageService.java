package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.Message;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    Message createMessage(Message message);
    Optional<Message> getMessageById(Integer id);
    List<Message> getMessagesByBookingId(Integer bookingId);
    List<Message> getMessagesBySenderId(Integer senderId);
    List<Message> getMessagesByReceiverId(Integer receiverId);
    Message updateMessage(Message message);
    void deleteMessage(Integer id);
}

