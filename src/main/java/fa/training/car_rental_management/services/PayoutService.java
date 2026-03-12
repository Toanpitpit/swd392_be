package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.Payout;
import fa.training.car_rental_management.enums.PayoutStatus;

import java.util.List;
import java.util.Optional;

public interface PayoutService {
    Payout createPayout(Payout payout);
    Optional<Payout> getPayoutById(Integer id);
    List<Payout> getPayoutsByOwnerId(Integer ownerId);
    List<Payout> getPayoutsByStatus(PayoutStatus status);
    List<Payout> getAllPayouts();
    Payout updatePayout(Payout payout);
    void deletePayout(Integer id);
}

