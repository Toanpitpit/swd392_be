package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.VehiclePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiclePhotoRepository extends JpaRepository<VehiclePhoto, Integer> {
    List<VehiclePhoto> findByVehicleId(Integer vehicleId);
}

