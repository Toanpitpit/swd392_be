package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.IdentityDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdentityDocumentRepository extends JpaRepository<IdentityDocument, Integer> {
    List<IdentityDocument> findByUserId(Integer userId);
}

