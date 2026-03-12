package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.Vehicle;
import fa.training.car_rental_management.enums.VehicleStatus;

import java.util.List;
import java.util.Optional;

public interface VehicleService {
    Vehicle createVehicle(Vehicle vehicle);
    Optional<Vehicle> getVehicleById(Integer id);
    Vehicle getVehicleByVin(String vin);
    List<Vehicle> getVehiclesByOwnerId(Integer ownerId);
    List<Vehicle> getVehiclesByStatus(VehicleStatus status);
    List<Vehicle> getAllVehicles();
    Vehicle updateVehicle(Vehicle vehicle);
    void deleteVehicle(Integer id);
}

