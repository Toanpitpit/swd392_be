package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.Payment;
import fa.training.car_rental_management.enums.PaymentStatus;
import fa.training.car_rental_management.repository.PaymentRepository;
import fa.training.car_rental_management.services.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment createPayment(Payment payment) {
        log.info("Creating payment for booking: {}", payment.getBookingId());
        return paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> getPaymentById(Integer id) {
        log.info("Fetching payment with id: {}", id);
        return paymentRepository.findById(id);
    }

    @Override
    public List<Payment> getPaymentsByBookingId(Integer bookingId) {
        log.info("Fetching payments for booking: {}", bookingId);
        return paymentRepository.findByBookingId(bookingId);
    }

    @Override
    public List<Payment> getPaymentsByPayerId(Integer payerId) {
        log.info("Fetching payments by payer: {}", payerId);
        return paymentRepository.findByPayerId(payerId);
    }

    @Override
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        log.info("Fetching payments with status: {}", status);
        return paymentRepository.findByStatus(status);
    }

    @Override
    public List<Payment> getAllPayments() {
        log.info("Fetching all payments");
        return paymentRepository.findAll();
    }

    @Override
    public Payment updatePayment(Payment payment) {
        log.info("Updating payment with id: {}", payment.getId());
        return paymentRepository.save(payment);
    }

    @Override
    public void deletePayment(Integer id) {
        log.info("Deleting payment with id: {}", id);
        paymentRepository.deleteById(id);
    }
}

