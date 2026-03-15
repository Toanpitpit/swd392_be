package fa.training.car_rental_management.services;

import fa.training.car_rental_management.entities.InspectionPhoto;

import java.util.List;
import java.util.Optional;

public interface InspectionPhotoService {
    InspectionPhoto createInspectionPhoto(InspectionPhoto inspectionPhoto);
    Optional<InspectionPhoto> getInspectionPhotoById(Integer id);
    List<InspectionPhoto> getPhotosByInspectionId(Integer inspectionId);
    InspectionPhoto updateInspectionPhoto(InspectionPhoto inspectionPhoto);
    void deleteInspectionPhoto(Integer id);
}

