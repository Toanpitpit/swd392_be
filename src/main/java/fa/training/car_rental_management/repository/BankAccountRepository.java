package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {
    List<BankAccount> findByUserId(Integer userId);
}

