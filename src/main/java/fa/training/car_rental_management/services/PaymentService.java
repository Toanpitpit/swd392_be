package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.Payment;
import fa.training.car_rental_management.enums.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment createPayment(Payment payment);
    Optional<Payment> getPaymentById(Integer id);
    List<Payment> getPaymentsByBookingId(Integer bookingId);
    List<Payment> getPaymentsByPayerId(Integer payerId);
    List<Payment> getPaymentsByStatus(PaymentStatus status);
    List<Payment> getAllPayments();
    Payment updatePayment(Payment payment);
    void deletePayment(Integer id);
}

