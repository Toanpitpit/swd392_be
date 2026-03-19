package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.dto.request.FinalizeInspectionRequest;
import fa.training.car_rental_management.dto.request.InspectionPickupRequest;
import fa.training.car_rental_management.dto.response.FinalizeInspectionResponse;
import fa.training.car_rental_management.dto.response.InspectionPickupResponse;
import fa.training.car_rental_management.entities.Inspection;
import fa.training.car_rental_management.enums.CarStatus;
import fa.training.car_rental_management.enums.InspectionType;
import fa.training.car_rental_management.services.InspectionService;
import fa.training.car_rental_management.util.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/inspections")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;
    private final JwtService jwtService;


    @PostMapping("/pickup")
    public ResponseEntity<ApiResponse<InspectionPickupResponse>> createPickupRecord(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody InspectionPickupRequest request) {
        log.info("Received request to create pickup record for booking: {}", request.getBookingId());
        try {
            String token = authHeader.substring(7);
            Integer inspectorId = jwtService.extractId(token)
                    .orElseThrow(() -> new RuntimeException("Invalid token: inspectorId not found"));

            InspectionPickupResponse response = inspectionService.createPickupRecord(request, inspectorId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Pickup record created successfully", response));
        } catch (Exception e) {
            log.error("Error creating pickup record: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create pickup record: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasAuthority('CAR_OWNER')")
    @PostMapping("/finalize-return")
    public ResponseEntity<ApiResponse<FinalizeInspectionResponse>> finalizeReturnInspection(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody FinalizeInspectionRequest request) {
        log.info("Received request to finalize return inspection for booking: {}", request.getBookingId());
        try {
            String token = authHeader.substring(7);
            Integer ownerId = jwtService.extractId(token)
                    .orElseThrow(() -> new RuntimeException("Invalid token: ownerId not found"));

            FinalizeInspectionResponse response = inspectionService.finalizeReturnInspection(request, ownerId);

            return ResponseEntity.ok(ApiResponse.success("Return inspection finalized successfully", response));
        } catch (Exception e) {
            log.error("Error finalizing return inspection: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to finalize return inspection: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Inspection>> getInspectionById(@PathVariable("id") Integer id) {
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


    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<List<Inspection>>> getInspectionsByBookingId(@PathVariable("bookingId") Integer bookingId) {
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
}

