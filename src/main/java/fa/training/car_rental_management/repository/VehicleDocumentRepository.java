package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.VehicleDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleDocumentRepository extends JpaRepository<VehicleDocument, Integer> {
    List<VehicleDocument> findByVehicleId(Integer vehicleId);
}

