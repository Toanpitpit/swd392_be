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
@RequestMapping("/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

}

