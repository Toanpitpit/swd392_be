package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.Vehicle;
import fa.training.car_rental_management.enums.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    List<Vehicle> findByOwnerId(Integer ownerId);
    List<Vehicle> findByStatus(VehicleStatus status);
    Vehicle findByVin(String vin);
}

