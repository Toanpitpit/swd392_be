package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.PaymentMethod;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodService {
    PaymentMethod createPaymentMethod(PaymentMethod paymentMethod);
    Optional<PaymentMethod> getPaymentMethodById(Integer id);
    List<PaymentMethod> getPaymentMethodsByUserId(Integer userId);
    PaymentMethod updatePaymentMethod(PaymentMethod paymentMethod);
    void deletePaymentMethod(Integer id);
}

