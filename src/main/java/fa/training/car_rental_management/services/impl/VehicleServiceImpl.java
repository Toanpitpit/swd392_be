package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.Vehicle;
import fa.training.car_rental_management.enums.VehicleStatus;
import fa.training.car_rental_management.repository.VehicleRepository;
import fa.training.car_rental_management.services.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public Vehicle createVehicle(Vehicle vehicle) {
        log.info("Creating vehicle with VIN: {}", vehicle.getVin());
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Optional<Vehicle> getVehicleById(Integer id) {
        log.info("Fetching vehicle with id: {}", id);
        return vehicleRepository.findById(id);
    }

    @Override
    public Vehicle getVehicleByVin(String vin) {
        log.info("Fetching vehicle with VIN: {}", vin);
        return vehicleRepository.findByVin(vin);
    }

    @Override
    public List<Vehicle> getVehiclesByOwnerId(Integer ownerId) {
        log.info("Fetching vehicles for owner: {}", ownerId);
        return vehicleRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Vehicle> getVehiclesByStatus(VehicleStatus status) {
        log.info("Fetching vehicles with status: {}", status);
        return vehicleRepository.findByStatus(status);
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        log.info("Fetching all vehicles");
        return vehicleRepository.findAll();
    }

    @Override
    public Vehicle updateVehicle(Vehicle vehicle) {
        log.info("Updating vehicle with id: {}", vehicle.getId());
        return vehicleRepository.save(vehicle);
    }

    @Override
    public void deleteVehicle(Integer id) {
        log.info("Deleting vehicle with id: {}", id);
        vehicleRepository.deleteById(id);
    }
}

