package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.Dispute;
import fa.training.car_rental_management.enums.DisputeStatus;

import java.util.List;
import java.util.Optional;

public interface DisputeService {
    Dispute createDispute(Dispute dispute);
    Optional<Dispute> getDisputeById(Integer id);
    List<Dispute> getDisputesByBookingId(Integer bookingId);
    List<Dispute> getDisputesByOpenedById(Integer openedById);
    List<Dispute> getDisputesByStatus(DisputeStatus status);
    List<Dispute> getAllDisputes();
    Dispute updateDispute(Dispute dispute);
    void deleteDispute(Integer id);
}

