package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.Message;
import fa.training.car_rental_management.repository.MessageRepository;
import fa.training.car_rental_management.services.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public Optional<Message> getMessageById(Integer id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> getMessagesByBookingId(Integer bookingId) {
        return messageRepository.findByBookingId(bookingId);
    }

    @Override
    public List<Message> getMessagesBySenderId(Integer senderId) {
        return messageRepository.findBySenderId(senderId);
    }

    @Override
    public List<Message> getMessagesByReceiverId(Integer receiverId) {
        return messageRepository.findByReceiverId(receiverId);
    }

    @Override
    public Message updateMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public void deleteMessage(Integer id) {
        messageRepository.deleteById(id);
    }
}

