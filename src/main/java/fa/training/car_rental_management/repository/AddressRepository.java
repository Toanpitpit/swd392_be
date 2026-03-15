package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    Optional<Address> findByVehicleId(Integer vehicleId);
}

