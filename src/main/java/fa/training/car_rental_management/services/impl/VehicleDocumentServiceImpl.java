package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.VehicleDocument;
import fa.training.car_rental_management.repository.VehicleDocumentRepository;
import fa.training.car_rental_management.services.VehicleDocumentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleDocumentServiceImpl implements VehicleDocumentService {

    private final VehicleDocumentRepository vehicleDocumentRepository;

    public VehicleDocumentServiceImpl(VehicleDocumentRepository vehicleDocumentRepository) {
        this.vehicleDocumentRepository = vehicleDocumentRepository;
    }

    @Override
    public VehicleDocument createVehicleDocument(VehicleDocument vehicleDocument) {
        return vehicleDocumentRepository.save(vehicleDocument);
    }

    @Override
    public Optional<VehicleDocument> getVehicleDocumentById(Integer id) {
        return vehicleDocumentRepository.findById(id);
    }

    @Override
    public List<VehicleDocument> getDocumentsByVehicleId(Integer vehicleId) {
        return vehicleDocumentRepository.findByVehicleId(vehicleId);
    }

    @Override
    public VehicleDocument updateVehicleDocument(VehicleDocument vehicleDocument) {
        return vehicleDocumentRepository.save(vehicleDocument);
    }

    @Override
    public void deleteVehicleDocument(Integer id) {
        vehicleDocumentRepository.deleteById(id);
    }
}

