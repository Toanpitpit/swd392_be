package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.Dispute;
import fa.training.car_rental_management.enums.DisputeStatus;
import fa.training.car_rental_management.repository.DisputeRepository;
import fa.training.car_rental_management.services.DisputeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class DisputeServiceImpl implements DisputeService {

    @Autowired
    private DisputeRepository disputeRepository;

    @Override
    public Dispute createDispute(Dispute dispute) {
        log.info("Creating dispute for booking: {}", dispute.getBookingId());
        return disputeRepository.save(dispute);
    }

    @Override
    public Optional<Dispute> getDisputeById(Integer id) {
        log.info("Fetching dispute with id: {}", id);
        return disputeRepository.findById(id);
    }

    @Override
    public List<Dispute> getDisputesByBookingId(Integer bookingId) {
        log.info("Fetching disputes for booking: {}", bookingId);
        return disputeRepository.findByBookingId(bookingId);
    }

    @Override
    public List<Dispute> getDisputesByOpenedById(Integer openedById) {
        log.info("Fetching disputes opened by: {}", openedById);
        return disputeRepository.findByOpenedById(openedById);
    }

    @Override
    public List<Dispute> getDisputesByStatus(DisputeStatus status) {
        log.info("Fetching disputes with status: {}", status);
        return disputeRepository.findByStatus(status);
    }

    @Override
    public List<Dispute> getAllDisputes() {
        log.info("Fetching all disputes");
        return disputeRepository.findAll();
    }

    @Override
    public Dispute updateDispute(Dispute dispute) {
        log.info("Updating dispute with id: {}", dispute.getId());
        return disputeRepository.save(dispute);
    }

    @Override
    public void deleteDispute(Integer id) {
        log.info("Deleting dispute with id: {}", id);
        disputeRepository.deleteById(id);
    }
}

