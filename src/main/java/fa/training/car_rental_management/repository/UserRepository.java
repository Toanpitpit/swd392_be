package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<users, Integer> {
    Optional<users> findByEmail(String email);
}

