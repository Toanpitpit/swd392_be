package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.InspectionPhoto;
import fa.training.car_rental_management.repository.InspectionPhotoRepository;
import fa.training.car_rental_management.services.InspectionPhotoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InspectionPhotoServiceImpl implements InspectionPhotoService {

    private final InspectionPhotoRepository inspectionPhotoRepository;

    public InspectionPhotoServiceImpl(InspectionPhotoRepository inspectionPhotoRepository) {
        this.inspectionPhotoRepository = inspectionPhotoRepository;
    }

    @Override
    public InspectionPhoto createInspectionPhoto(InspectionPhoto inspectionPhoto) {
        return inspectionPhotoRepository.save(inspectionPhoto);
    }

    @Override
    public Optional<InspectionPhoto> getInspectionPhotoById(Integer id) {
        return inspectionPhotoRepository.findById(id);
    }

    @Override
    public List<InspectionPhoto> getPhotosByInspectionId(Integer inspectionId) {
        return inspectionPhotoRepository.findByInspectionId(inspectionId);
    }

    @Override
    public InspectionPhoto updateInspectionPhoto(InspectionPhoto inspectionPhoto) {
        return inspectionPhotoRepository.save(inspectionPhoto);
    }

    @Override
    public void deleteInspectionPhoto(Integer id) {
        inspectionPhotoRepository.deleteById(id);
    }
}

