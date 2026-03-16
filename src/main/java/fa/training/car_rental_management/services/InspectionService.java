package fa.training.car_rental_management.services;

import fa.training.car_rental_management.dto.request.InspectionPickupRequest;
import fa.training.car_rental_management.dto.response.InspectionPickupResponse;
import fa.training.car_rental_management.entities.Inspection;
import fa.training.car_rental_management.enums.InspectionType;

import java.util.List;
import java.util.Optional;

public interface InspectionService {
    Inspection createInspection(Inspection inspection);
    Optional<Inspection> getInspectionById(Integer id);
    List<Inspection> getInspectionsByBookingId(Integer bookingId);
    List<Inspection> getInspectionsByInspectorId(Integer inspectorId);
    List<Inspection> getInspectionsByType(InspectionType type);
    List<Inspection> getAllInspections();
    Inspection updateInspection(Inspection inspection);
    void deleteInspection(Integer id);
    InspectionPickupResponse createPickupRecord(InspectionPickupRequest request, Integer inspectorId);
}

