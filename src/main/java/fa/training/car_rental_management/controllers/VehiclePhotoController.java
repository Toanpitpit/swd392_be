package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.entities.VehiclePhoto;
import fa.training.car_rental_management.services.VehiclePhotoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/vehicle-photos")
@CrossOrigin(origins = "*")
public class VehiclePhotoController {

    @Autowired
    private VehiclePhotoService vehiclePhotoService;

    @PostMapping
    public ResponseEntity<ApiResponse<VehiclePhoto>> createVehiclePhoto(@RequestBody VehiclePhoto vehiclePhoto) {
        try {
            VehiclePhoto createdPhoto = vehiclePhotoService.createVehiclePhoto(vehiclePhoto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Vehicle photo created successfully", createdPhoto));
        } catch (Exception e) {
            log.error("Error creating vehicle photo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create vehicle photo: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehiclePhoto>> getVehiclePhotoById(@PathVariable("id") Integer id) {
        try {
            Optional<VehiclePhoto> photo = vehiclePhotoService.getVehiclePhotoById(id);
            return photo.map(p -> ResponseEntity.ok(ApiResponse.success("Vehicle photo retrieved", p)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Vehicle photo not found")));
        } catch (Exception e) {
            log.error("Error retrieving vehicle photo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving vehicle photo: " + e.getMessage()));
        }
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<List<VehiclePhoto>>> getPhotosByVehicleId(@PathVariable("vehicleId") Integer vehicleId) {
        try {
            List<VehiclePhoto> photos = vehiclePhotoService.getPhotosByVehicleId(vehicleId);
            return ResponseEntity.ok(ApiResponse.success("Vehicle photos retrieved", photos));
        } catch (Exception e) {
            log.error("Error retrieving vehicle photos", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving vehicle photos: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehiclePhoto>> updateVehiclePhoto(@PathVariable("id") Integer id, @RequestBody VehiclePhoto vehiclePhoto) {
        try {
            vehiclePhoto.setId(id);
            VehiclePhoto updatedPhoto = vehiclePhotoService.updateVehiclePhoto(vehiclePhoto);
            return ResponseEntity.ok(ApiResponse.success("Vehicle photo updated successfully", updatedPhoto));
        } catch (Exception e) {
            log.error("Error updating vehicle photo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update vehicle photo: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVehiclePhoto(@PathVariable("id") Integer id) {
        try {
            vehiclePhotoService.deleteVehiclePhoto(id);
            return ResponseEntity.ok(ApiResponse.success("Vehicle photo deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting vehicle photo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete vehicle photo: " + e.getMessage()));
        }
    }
}

