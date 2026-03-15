package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.Incident;
import fa.training.car_rental_management.repository.IncidentRepository;
import fa.training.car_rental_management.services.IncidentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentServiceImpl(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Override
    public Incident createIncident(Incident incident) {
        return incidentRepository.save(incident);
    }

    @Override
    public Optional<Incident> getIncidentById(Integer id) {
        return incidentRepository.findById(id);
    }

    @Override
    public List<Incident> getIncidentsByBookingId(Integer bookingId) {
        return incidentRepository.findByBookingId(bookingId);
    }

    @Override
    public Incident updateIncident(Incident incident) {
        return incidentRepository.save(incident);
    }

    @Override
    public void deleteIncident(Integer id) {
        incidentRepository.deleteById(id);
    }
}

