package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    Notification createNotification(Notification notification);
    Optional<Notification> getNotificationById(Integer id);
    List<Notification> getNotificationsByUserId(Integer userId);
    Notification updateNotification(Notification notification);
    void deleteNotification(Integer id);
}

