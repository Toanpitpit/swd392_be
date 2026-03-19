package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.entities.VehicleDocument;
import fa.training.car_rental_management.services.VehicleDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/vehicle-documents")
@CrossOrigin(origins = "*")
public class VehicleDocumentController {

    @Autowired
    private VehicleDocumentService vehicleDocumentService;

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleDocument>> createVehicleDocument(@RequestBody VehicleDocument vehicleDocument) {
        try {
            VehicleDocument createdDocument = vehicleDocumentService.createVehicleDocument(vehicleDocument);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Vehicle document created successfully", createdDocument));
        } catch (Exception e) {
            log.error("Error creating vehicle document", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create vehicle document: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleDocument>> getVehicleDocumentById(@PathVariable("id") Integer id) {
        try {
            Optional<VehicleDocument> document = vehicleDocumentService.getVehicleDocumentById(id);
            return document.map(d -> ResponseEntity.ok(ApiResponse.success("Vehicle document retrieved", d)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Vehicle document not found")));
        } catch (Exception e) {
            log.error("Error retrieving vehicle document", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving vehicle document: " + e.getMessage()));
        }
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<List<VehicleDocument>>> getDocumentsByVehicleId(@PathVariable("vehicleId") Integer vehicleId) {
        try {
            List<VehicleDocument> documents = vehicleDocumentService.getDocumentsByVehicleId(vehicleId);
            return ResponseEntity.ok(ApiResponse.success("Vehicle documents retrieved", documents));
        } catch (Exception e) {
            log.error("Error retrieving vehicle documents", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error retrieving vehicle documents: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleDocument>> updateVehicleDocument(@PathVariable("id") Integer id, @RequestBody VehicleDocument vehicleDocument) {
        try {
            vehicleDocument.setId(id);
            VehicleDocument updatedDocument = vehicleDocumentService.updateVehicleDocument(vehicleDocument);
            return ResponseEntity.ok(ApiResponse.success("Vehicle document updated successfully", updatedDocument));
        } catch (Exception e) {
            log.error("Error updating vehicle document", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update vehicle document: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVehicleDocument(@PathVariable("id") Integer id) {
        try {
            vehicleDocumentService.deleteVehicleDocument(id);
            return ResponseEntity.ok(ApiResponse.success("Vehicle document deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting vehicle document", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete vehicle document: " + e.getMessage()));
        }
    }
}

