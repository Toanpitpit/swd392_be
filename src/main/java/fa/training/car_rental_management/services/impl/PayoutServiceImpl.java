package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.Payout;
import fa.training.car_rental_management.enums.PayoutStatus;
import fa.training.car_rental_management.repository.PayoutRepository;
import fa.training.car_rental_management.services.PayoutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class PayoutServiceImpl implements PayoutService {

    @Autowired
    private PayoutRepository payoutRepository;

    @Override
    public Payout createPayout(Payout payout) {
        log.info("Creating payout for owner: {}", payout.getOwnerId());
        return payoutRepository.save(payout);
    }

    @Override
    public Optional<Payout> getPayoutById(Integer id) {
        log.info("Fetching payout with id: {}", id);
        return payoutRepository.findById(id);
    }

    @Override
    public List<Payout> getPayoutsByOwnerId(Integer ownerId) {
        log.info("Fetching payouts for owner: {}", ownerId);
        return payoutRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Payout> getPayoutsByStatus(PayoutStatus status) {
        log.info("Fetching payouts with status: {}", status);
        return payoutRepository.findByStatus(status);
    }

    @Override
    public List<Payout> getAllPayouts() {
        log.info("Fetching all payouts");
        return payoutRepository.findAll();
    }

    @Override
    public Payout updatePayout(Payout payout) {
        log.info("Updating payout with id: {}", payout.getId());
        return payoutRepository.save(payout);
    }

    @Override
    public void deletePayout(Integer id) {
        log.info("Deleting payout with id: {}", id);
        payoutRepository.deleteById(id);
    }
}

