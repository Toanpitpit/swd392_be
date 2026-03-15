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
@RequestMapping("/api/incidents")
@CrossOrigin(origins = "*")
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Incident>> createIncident(@RequestBody Incident incident) {
        try {
            Incident createdIncident = incidentService.createIncident(incident);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Incident created successfully", createdIncident));
        } catch (Exception e) {
            log.error("Error creating incident", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create incident: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Incident>> getIncidentById(@PathVariable Integer id) {
        try {
            Optional<Incident> incident = incidentService.getIncidentById(id);
            return incident.map(inc -> ResponseEntity.ok(ApiResponse.success("Incident retrieved", inc)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Incident not found")));
        } catch (Exception e) {
            log.error("Error retrieving incident", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving incident: " + e.getMessage()));
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<List<Incident>>> getIncidentsByBookingId(@PathVariable Integer bookingId) {
        try {
            List<Incident> incidents = incidentService.getIncidentsByBookingId(bookingId);
            return ResponseEntity.ok(ApiResponse.success("Incidents retrieved", incidents));
        } catch (Exception e) {
            log.error("Error retrieving incidents", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving incidents: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Incident>> updateIncident(@PathVariable Integer id, @RequestBody Incident incident) {
        try {
            incident.setId(id);
            Incident updatedIncident = incidentService.updateIncident(incident);
            return ResponseEntity.ok(ApiResponse.success("Incident updated successfully", updatedIncident));
        } catch (Exception e) {
            log.error("Error updating incident", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update incident: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIncident(@PathVariable Integer id) {
        try {
            incidentService.deleteIncident(id);
            return ResponseEntity.ok(ApiResponse.success("Incident deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting incident", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete incident: " + e.getMessage()));
        }
    }
}

