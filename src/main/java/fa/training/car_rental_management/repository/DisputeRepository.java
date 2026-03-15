package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.Dispute;
import fa.training.car_rental_management.enums.DisputeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Integer> {
    List<Dispute> findByBookingId(Integer bookingId);
    List<Dispute> findByOpenedById(Integer openedById);
    List<Dispute> findByStatus(DisputeStatus status);
}

