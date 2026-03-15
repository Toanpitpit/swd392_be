package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.PaymentMethod;
import fa.training.car_rental_management.repository.PaymentMethodRepository;
import fa.training.car_rental_management.services.PaymentMethodService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodServiceImpl(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public PaymentMethod createPaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public Optional<PaymentMethod> getPaymentMethodById(Integer id) {
        return paymentMethodRepository.findById(id);
    }

    @Override
    public List<PaymentMethod> getPaymentMethodsByUserId(Integer userId) {
        return paymentMethodRepository.findByUserId(userId);
    }

    @Override
    public PaymentMethod updatePaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public void deletePaymentMethod(Integer id) {
        paymentMethodRepository.deleteById(id);
    }
}

