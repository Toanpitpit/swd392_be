package fa.training.car_rental_management.services;

import fa.training.car_rental_management.dto.response.VehicleResponseDTO;
import fa.training.car_rental_management.entities.Vehicle;
import fa.training.car_rental_management.enums.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface VehicleService {
    VehicleResponseDTO getVehicleById(Integer id);
//    List<Vehicle> getVehiclesByOwnerId(Integer ownerId);
//    List<Vehicle> getVehiclesByStatus(VehicleStatus status);
//    Page<VehicleResponseDTO> getAllVehicles(Pageable pageable);


    Page<VehicleResponseDTO> getAvailableVehicles(Pageable pageable, LocalDateTime startDate , LocalDateTime endDate);
    Page<VehicleResponseDTO> searchVehiclesForCarOwner(
            String model,
            VehicleStatus status,
            Integer ownerId,
            Pageable pageable);
    Page<VehicleResponseDTO> searchVehiclesForElse(
            String model,
            String city,
            VehicleStatus status,
            Pageable pageable,
            LocalDateTime startDate,
            LocalDateTime endDate);
}



