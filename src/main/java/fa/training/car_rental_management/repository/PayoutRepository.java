package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.Payout;
import fa.training.car_rental_management.enums.PayoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, Integer> {
    List<Payout> findByOwnerId(Integer ownerId);
    List<Payout> findByStatus(PayoutStatus status);
}

