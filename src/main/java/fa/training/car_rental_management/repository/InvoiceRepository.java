package fa.training.car_rental_management.repository;

import fa.training.car_rental_management.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    Optional<Invoice> findByBookingId(Integer bookingId);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}

