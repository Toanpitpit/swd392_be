package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.PlatformContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlatformContentRepository extends JpaRepository<PlatformContent, Integer> {
    Optional<PlatformContent> findByKey(String key);
}

