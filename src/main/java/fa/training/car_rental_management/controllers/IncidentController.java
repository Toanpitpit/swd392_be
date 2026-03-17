package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.entities.Incident;
import fa.training.car_rental_management.services.IncidentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/incidents")
@CrossOrigin(origins = "*")
public class IncidentController {


}

