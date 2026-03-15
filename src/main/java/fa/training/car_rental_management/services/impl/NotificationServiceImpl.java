package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.Notification;
import fa.training.car_rental_management.repository.NotificationRepository;
import fa.training.car_rental_management.services.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Optional<Notification> getNotificationById(Integer id) {
        return notificationRepository.findById(id);
    }

    @Override
    public List<Notification> getNotificationsByUserId(Integer userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public Notification updateNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public void deleteNotification(Integer id) {
        notificationRepository.deleteById(id);
    }
}

