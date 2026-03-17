package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.dto.request.SearchVehicalRequestDTO;
import fa.training.car_rental_management.dto.response.VehicleResponseDTO;
import fa.training.car_rental_management.enums.UserRole;
import fa.training.car_rental_management.services.VehicleService;
import fa.training.car_rental_management.util.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static fa.training.car_rental_management.enums.UserRole.CUSTOMER;

/**
 * VehicleController - API để search và filter xe
 *
 * Endpoints:
 * - GET /api/vehicles/available: Lấy xe có status = AVAILABLE (phân trang)
 * - GET /api/vehicles/search: Search xe với multiple filters (model, city, fuelType, status)
 */
@Slf4j
@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VehicleController {

    private final VehicleService vehicleService;
    private final JwtService jwtService;

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<Page<VehicleResponseDTO>>> getAvailableVehicles(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam("startDate") LocalDateTime startDate ,
            @RequestParam("endDate") LocalDateTime endDate) {
        try {
            log.info("Getting available vehicles - page: {}, size: {}", page, size);

            Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            Page<VehicleResponseDTO> availableVehicles = vehicleService.getAvailableVehicles(pageable,startDate,endDate);

            return ResponseEntity.ok(
                ApiResponse.success("Available vehicles retrieved successfully", availableVehicles)
            );
        } catch (Exception e) {
            log.error("Error fetching available vehicles: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch available vehicles: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponseDTO>> getVehicleById(@PathVariable("id") Integer id) {
        log.info("Fetching details for vehicle ID: {}", id);
        VehicleResponseDTO vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle details retrieved", vehicle));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<VehicleResponseDTO>>> searchVehicles(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid SearchVehicalRequestDTO dto) {
        try {
            log.info("Searching vehicles - model: {}, city: {}, status: {}, page: {}, size: {}",
                    dto.getModel(), dto.getCity(), dto.getStatus(), dto.getPage(), dto.getSize());

            Integer currentUserId = 2;
            UserRole role = UserRole.CUSTOMER;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                currentUserId = jwtService.extractId(token).orElse(null);
                log.info("Extracted user ID from token: {}", currentUserId);
                String extractedRole = jwtService.extractRole(token).orElse("CUSTOMER");
                try {
                    role = UserRole.valueOf(extractedRole);
                    log.info("Extracted role from token: {}", role);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid role in token: {}, defaulting to CUSTOMER", extractedRole);
                }
            }

            Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize());
            Page<VehicleResponseDTO> searchResults;

            if (role == UserRole.CAR_OWNER && currentUserId != null) {
                searchResults = vehicleService.searchVehiclesForCarOwner(
                        dto.getModel(),
                        dto.getStatus(),
                        currentUserId,
                        pageable);
            } else {
                searchResults = vehicleService.searchVehiclesForElse(
                        dto.getModel(),
                        dto.getCity(),
                        dto.getStatus(),
                        pageable,
                        dto.getStartDate(),
                        dto.getEndDate());
            }

            return ResponseEntity.ok(
                    ApiResponse.success("Search completed successfully", searchResults)
            );

        } catch (Exception e) {
            log.error("Error searching vehicles: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to search vehicles: " + e.getMessage()));
        }
    }
}

