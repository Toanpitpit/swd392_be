package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.ModerationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModerationLogRepository extends JpaRepository<ModerationLog, Integer> {
    List<ModerationLog> findByModeratorId(Integer moderatorId);
    List<ModerationLog> findByTargetEntityId(Integer targetEntityId);
}

