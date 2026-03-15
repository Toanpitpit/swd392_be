package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Integer> {
    List<Incident> findByBookingId(Integer bookingId);
}

