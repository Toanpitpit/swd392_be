package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.VehiclePhoto;

import java.util.List;
import java.util.Optional;

public interface VehiclePhotoService {
    VehiclePhoto createVehiclePhoto(VehiclePhoto vehiclePhoto);
    Optional<VehiclePhoto> getVehiclePhotoById(Integer id);
    List<VehiclePhoto> getPhotosByVehicleId(Integer vehicleId);
    VehiclePhoto updateVehiclePhoto(VehiclePhoto vehiclePhoto);
    void deleteVehiclePhoto(Integer id);
}

