package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.VehicleDocument;

import java.util.List;
import java.util.Optional;

public interface VehicleDocumentService {
    VehicleDocument createVehicleDocument(VehicleDocument vehicleDocument);
    Optional<VehicleDocument> getVehicleDocumentById(Integer id);
    List<VehicleDocument> getDocumentsByVehicleId(Integer vehicleId);
    VehicleDocument updateVehicleDocument(VehicleDocument vehicleDocument);
    void deleteVehicleDocument(Integer id);
}

