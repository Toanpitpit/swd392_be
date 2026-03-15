package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.Inspection;
import fa.training.car_rental_management.enums.InspectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Integer> {
    List<Inspection> findByBookingId(Integer bookingId);
    List<Inspection> findByInspectorId(Integer inspectorId);
    List<Inspection> findByType(InspectionType type);
}

