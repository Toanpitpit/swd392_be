package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.dto.response.UsersResponseDTO;
import fa.training.car_rental_management.dto.response.VehicleResponseDTO;
import fa.training.car_rental_management.entities.Vehicle;
import fa.training.car_rental_management.enums.FuelType;
import fa.training.car_rental_management.enums.VehicleStatus;
import fa.training.car_rental_management.repository.AddressRepository;
import fa.training.car_rental_management.repository.VehicleRepository;
import fa.training.car_rental_management.services.VehicleService;
import fa.training.car_rental_management.util.VehicleSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final AddressRepository addressRepository;


    @Override
    @Transactional(readOnly = true)
    public VehicleResponseDTO getVehicleById(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id: " + id));
        return mapToVehicleResponseDTO(vehicle);
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
    public Page<VehicleResponseDTO> getAllVehicles(Pageable pageable) {
        Page<Vehicle> lstVe =  vehicleRepository.findAll(pageable);
        return lstVe.map(this::mapToVehicleResponseDTO);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<VehicleResponseDTO> getAvailableVehicles(Pageable pageable, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching available vehicles between {} and {} with pagination: page={}, size={}",
                startDate, endDate, pageable.getPageNumber(), pageable.getPageSize());

        if (startDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        Specification<Vehicle> spec = VehicleSpecifications.isAvailable(startDate, endDate);

        Page<Vehicle> vehiclePage = vehicleRepository.findAll(spec, pageable);

        return vehiclePage.map(this::mapToVehicleResponseDTO);
    }

    private VehicleResponseDTO mapToVehicleResponseDTO(Vehicle vehicle) {
        VehicleResponseDTO dto = new VehicleResponseDTO();
        BeanUtils.copyProperties(vehicle, dto);
        
        if (vehicle.getOwner() != null) {
            UsersResponseDTO userResponse = new UsersResponseDTO();
            BeanUtils.copyProperties(vehicle.getOwner(), userResponse);
            dto.setOwner(userResponse);
        }
        return dto;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<VehicleResponseDTO> searchVehiclesForCarOwner(
            String model, VehicleStatus status, Integer ownerId, Pageable pageable) {

        log.info("Owner Management - Fetching vehicles for ownerId: {}", ownerId);

        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID is required for this operation");
        }

        Specification<Vehicle> spec = Specification.where((root, query, cb) ->
                cb.equal(root.get("owner").get("id"), ownerId));

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (model != null && !model.isBlank()) {
            spec = spec.and(VehicleSpecifications.hasModel(model));
        }

        return vehicleRepository.findAll(spec, pageable).map(this::mapToVehicleResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleResponseDTO> searchVehiclesForElse(
            String model, String city, VehicleStatus status ,Pageable pageable,
            LocalDateTime startDate, LocalDateTime endDate) {

        log.info("Customer Search - Finding available vehicles");

        Specification<Vehicle> spec = Specification.where((root, query, cb) ->
                cb.equal(root.get("status"), VehicleStatus.ACTIVE));


        if (startDate != null && endDate != null) {
            spec = spec.and(VehicleSpecifications.isAvailable(startDate, endDate));
        }

        if (city != null && !city.isBlank()) {
            spec = spec.and(VehicleSpecifications.hasCity(city));
        }

        if (status != null && status != VehicleStatus.ACTIVE) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (model != null && !model.isBlank()) {
            spec = spec.and(VehicleSpecifications.hasModel(model));
        }


        return vehicleRepository.findAll(spec, pageable).map(this::mapToVehicleResponseDTO);
    }
}



