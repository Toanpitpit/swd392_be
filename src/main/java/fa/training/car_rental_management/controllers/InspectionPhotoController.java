package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.entities.InspectionPhoto;
import fa.training.car_rental_management.services.InspectionPhotoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/inspection-photos")
@CrossOrigin(origins = "*")
public class InspectionPhotoController {

    @Autowired
    private InspectionPhotoService inspectionPhotoService;

    @PostMapping
    public ResponseEntity<ApiResponse<InspectionPhoto>> createInspectionPhoto(@RequestBody InspectionPhoto inspectionPhoto) {
        try {
            InspectionPhoto createdPhoto = inspectionPhotoService.createInspectionPhoto(inspectionPhoto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Inspection photo created successfully", createdPhoto));
        } catch (Exception e) {
            log.error("Error creating inspection photo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create inspection photo: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InspectionPhoto>> getInspectionPhotoById(@PathVariable Integer id) {
        try {
            Optional<InspectionPhoto> photo = inspectionPhotoService.getInspectionPhotoById(id);
            return photo.map(p -> ResponseEntity.ok(ApiResponse.success("Inspection photo retrieved", p)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Inspection photo not found")));
        } catch (Exception e) {
            log.error("Error retrieving inspection photo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving inspection photo: " + e.getMessage()));
        }
    }

    @GetMapping("/inspection/{inspectionId}")
    public ResponseEntity<ApiResponse<List<InspectionPhoto>>> getPhotosByInspectionId(@PathVariable Integer inspectionId) {
        try {
            List<InspectionPhoto> photos = inspectionPhotoService.getPhotosByInspectionId(inspectionId);
            return ResponseEntity.ok(ApiResponse.success("Inspection photos retrieved", photos));
        } catch (Exception e) {
            log.error("Error retrieving inspection photos", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving inspection photos: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InspectionPhoto>> updateInspectionPhoto(@PathVariable Integer id, @RequestBody InspectionPhoto inspectionPhoto) {
        try {
            inspectionPhoto.setId(id);
            InspectionPhoto updatedPhoto = inspectionPhotoService.updateInspectionPhoto(inspectionPhoto);
            return ResponseEntity.ok(ApiResponse.success("Inspection photo updated successfully", updatedPhoto));
        } catch (Exception e) {
            log.error("Error updating inspection photo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update inspection photo: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInspectionPhoto(@PathVariable Integer id) {
        try {
            inspectionPhotoService.deleteInspectionPhoto(id);
            return ResponseEntity.ok(ApiResponse.success("Inspection photo deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting inspection photo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete inspection photo: " + e.getMessage()));
        }
    }
}

