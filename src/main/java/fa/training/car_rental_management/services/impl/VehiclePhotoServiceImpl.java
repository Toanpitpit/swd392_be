package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.VehiclePhoto;
import fa.training.car_rental_management.repository.VehiclePhotoRepository;
import fa.training.car_rental_management.services.VehiclePhotoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehiclePhotoServiceImpl implements VehiclePhotoService {

    private final VehiclePhotoRepository vehiclePhotoRepository;

    public VehiclePhotoServiceImpl(VehiclePhotoRepository vehiclePhotoRepository) {
        this.vehiclePhotoRepository = vehiclePhotoRepository;
    }

    @Override
    public VehiclePhoto createVehiclePhoto(VehiclePhoto vehiclePhoto) {
        return vehiclePhotoRepository.save(vehiclePhoto);
    }

    @Override
    public Optional<VehiclePhoto> getVehiclePhotoById(Integer id) {
        return vehiclePhotoRepository.findById(id);
    }

    @Override
    public List<VehiclePhoto> getPhotosByVehicleId(Integer vehicleId) {
        return vehiclePhotoRepository.findByVehicleId(vehicleId);
    }

    @Override
    public VehiclePhoto updateVehiclePhoto(VehiclePhoto vehiclePhoto) {
        return vehiclePhotoRepository.save(vehiclePhoto);
    }

    @Override
    public void deleteVehiclePhoto(Integer id) {
        vehiclePhotoRepository.deleteById(id);
    }
}

