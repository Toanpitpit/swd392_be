package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Integer> {
    Optional<UserSettings> findByUserId(Integer userId);
}

