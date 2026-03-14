package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.Incident;

import java.util.List;
import java.util.Optional;

public interface IncidentService {
    Incident createIncident(Incident incident);
    Optional<Incident> getIncidentById(Integer id);
    List<Incident> getIncidentsByBookingId(Integer bookingId);
    Incident updateIncident(Incident incident);
    void deleteIncident(Integer id);
}

