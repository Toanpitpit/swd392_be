package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.entities.Inspection;
import fa.training.car_rental_management.enums.CarStatus;
import fa.training.car_rental_management.enums.InspectionType;
import fa.training.car_rental_management.services.InspectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/inspections")
@CrossOrigin(origins = "*")
public class InspectionController {

    @Autowired
    private InspectionService inspectionService;

    /**
     * Create inspection (PICKUP or RETURN)
     * POST /api/inspections
     */giti
    @PostMapping
    public ResponseEntity<ApiResponse<Inspection>> createInspection(@RequestBody Inspection inspection) {
        try {
            log.info("Creating {} inspection for booking: {}", 
                    inspection.getType(), inspection.getBookingId());
            
            Inspection createdInspection = inspectionService.createInspection(inspection);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Inspection created successfully", createdInspection));
        } catch (Exception e) {
            log.error("Error creating inspection", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create inspection: " + e.getMessage()));
        }
    }

    /**
     * Get inspection by ID
     * GET /api/inspections/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Inspection>> getInspectionById(@PathVariable Integer id) {
        try {
            log.info("Fetching inspection with id: {}", id);
            
            Optional<Inspection> inspection = inspectionService.getInspectionById(id);
            
            if (inspection.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Inspection retrieved successfully", inspection.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Inspection not found with id: " + id));
            }
        } catch (Exception e) {
            log.error("Error fetching inspection", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching inspection"));
        }
    }

    /**
     * Get inspections by booking ID
     * GET /api/inspections/booking/{bookingId}
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<List<Inspection>>> getInspectionsByBookingId(@PathVariable Integer bookingId) {
        try {
            log.info("Fetching inspections for booking: {}", bookingId);
            
            List<Inspection> inspections = inspectionService.getInspectionsByBookingId(bookingId);
            
            return ResponseEntity.ok(ApiResponse.success(
                    "Inspections retrieved successfully", inspections));
        } catch (Exception e) {
            log.error("Error fetching inspections", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching inspections"));
        }
    }

    /**
     * Get inspections by inspector ID
     * GET /api/inspections/inspector/{inspectorId}
     */
    @GetMapping("/inspector/{inspectorId}")
    public ResponseEntity<ApiResponse<List<Inspection>>> getInspectionsByInspectorId(@PathVariable Integer inspectorId) {
        try {
            log.info("Fetching inspections by inspector: {}", inspectorId);
            
            List<Inspection> inspections = inspectionService.getInspectionsByInspectorId(inspectorId);
            
            return ResponseEntity.ok(ApiResponse.success(
                    "Inspections retrieved successfully", inspections));
        } catch (Exception e) {
            log.error("Error fetching inspections", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching inspections"));
        }
    }

    /**
     * Get inspections by type
     * GET /api/inspections/type/{type}
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<Inspection>>> getInspectionsByType(@PathVariable InspectionType type) {
        try {
            log.info("Fetching {} inspections", type);
            
            List<Inspection> inspections = inspectionService.getInspectionsByType(type);
            
            return ResponseEntity.ok(ApiResponse.success(
                    "Inspections retrieved successfully", inspections));
        } catch (Exception e) {
            log.error("Error fetching inspections", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching inspections"));
        }
    }

    /**
     * Get all inspections
     * GET /api/inspections
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Inspection>>> getAllInspections() {
        try {
            log.info("Fetching all inspections");
            
            List<Inspection> inspections = inspectionService.getAllInspections();
            
            return ResponseEntity.ok(ApiResponse.success(
                    "All inspections retrieved successfully", inspections));
        } catch (Exception e) {
            log.error("Error fetching inspections", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching inspections"));
        }
    }

    /**
     * Create pickup inspection
     * POST /api/inspections/pickup
     */
    @PostMapping("/pickup")
    public ResponseEntity<ApiResponse<Inspection>> createPickupInspection(
            @RequestParam Integer bookingId,
            @RequestParam Integer inspectorId,
            @RequestParam CarStatus carStatus,
            @RequestParam(required = false) String comments) {
        try {
            log.info("Creating pickup inspection for booking: {}", bookingId);
            
            Inspection inspection = Inspection.builder()
                    .bookingId(bookingId)
                    .inspectorId(inspectorId)
                    .type(InspectionType.PICKUP)
                    .carStatus(carStatus)
                    .comments(comments)
                    .date(LocalDateTime.now())
                    .build();
            
            Inspection createdInspection = inspectionService.createInspection(inspection);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Pickup inspection created successfully", createdInspection));
        } catch (Exception e) {
            log.error("Error creating pickup inspection", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create pickup inspection: " + e.getMessage()));
        }
    }

    /**
     * Create return inspection
     * POST /api/inspections/return
     */
    @PostMapping("/return")
    public ResponseEntity<ApiResponse<Inspection>> createReturnInspection(
            @RequestParam Integer bookingId,
            @RequestParam Integer inspectorId,
            @RequestParam CarStatus carStatus,
            @RequestParam(required = false) String comments) {
        try {
            log.info("Creating return inspection for booking: {}", bookingId);
            
            Inspection inspection = Inspection.builder()
                    .bookingId(bookingId)
                    .inspectorId(inspectorId)
                    .type(InspectionType.RETURN)
                    .carStatus(carStatus)
                    .comments(comments)
                    .date(LocalDateTime.now())
                    .build();
            
            Inspection createdInspection = inspectionService.createInspection(inspection);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Return inspection created successfully", createdInspection));
        } catch (Exception e) {
            log.error("Error creating return inspection", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create return inspection: " + e.getMessage()));
        }
    }

    /**
     * Update inspection
     * PUT /api/inspections/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Inspection>> updateInspection(
            @PathVariable Integer id,
            @RequestBody Inspection inspection) {
        try {
            log.info("Updating inspection with id: {}", id);
            
            Optional<Inspection> existingInspection = inspectionService.getInspectionById(id);
            
            if (existingInspection.isPresent()) {
                inspection.setId(id);
                Inspection updatedInspection = inspectionService.updateInspection(inspection);
                
                return ResponseEntity.ok(ApiResponse.success(
                        "Inspection updated successfully", updatedInspection));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Inspection not found with id: " + id));
            }
        } catch (Exception e) {
            log.error("Error updating inspection", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update inspection: " + e.getMessage()));
        }
    }

    /**
     * Delete inspection
     * DELETE /api/inspections/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInspection(@PathVariable Integer id) {
        try {
            log.info("Deleting inspection with id: {}", id);
            
            Optional<Inspection> existingInspection = inspectionService.getInspectionById(id);
            
            if (existingInspection.isPresent()) {
                inspectionService.deleteInspection(id);
                return ResponseEntity.ok(ApiResponse.success("Inspection deleted successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Inspection not found with id: " + id));
            }
        } catch (Exception e) {
            log.error("Error deleting inspection", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete inspection: " + e.getMessage()));
        }
    }
}

