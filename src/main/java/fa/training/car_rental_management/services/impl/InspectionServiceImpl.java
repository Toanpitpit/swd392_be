package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.Inspection;
import fa.training.car_rental_management.enums.InspectionType;
import fa.training.car_rental_management.repository.InspectionRepository;
import fa.training.car_rental_management.services.InspectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class InspectionServiceImpl implements InspectionService {

    @Autowired
    private InspectionRepository inspectionRepository;

    @Override
    public Inspection createInspection(Inspection inspection) {
        log.info("Creating inspection for booking: {}", inspection.getBookingId());
        return inspectionRepository.save(inspection);
    }

    @Override
    public Optional<Inspection> getInspectionById(Integer id) {
        log.info("Fetching inspection with id: {}", id);
        return inspectionRepository.findById(id);
    }

    @Override
    public List<Inspection> getInspectionsByBookingId(Integer bookingId) {
        log.info("Fetching inspections for booking: {}", bookingId);
        return inspectionRepository.findByBookingId(bookingId);
    }

    @Override
    public List<Inspection> getInspectionsByInspectorId(Integer inspectorId) {
        log.info("Fetching inspections by inspector: {}", inspectorId);
        return inspectionRepository.findByInspectorId(inspectorId);
    }

    @Override
    public List<Inspection> getInspectionsByType(InspectionType type) {
        log.info("Fetching inspections with type: {}", type);
        return inspectionRepository.findByType(type);
    }

    @Override
    public List<Inspection> getAllInspections() {
        log.info("Fetching all inspections");
        return inspectionRepository.findAll();
    }

    @Override
    public Inspection updateInspection(Inspection inspection) {
        log.info("Updating inspection with id: {}", inspection.getId());
        return inspectionRepository.save(inspection);
    }

    @Override
    public void deleteInspection(Integer id) {
        log.info("Deleting inspection with id: {}", id);
        inspectionRepository.deleteById(id);
    }
}

