package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    List<Wishlist> findByCustomerId(Integer customerId);
    List<Wishlist> findByVehicleId(Integer vehicleId);
}

