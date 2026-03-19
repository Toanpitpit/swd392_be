package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.dto.request.InspectionPickupRequest;
import fa.training.car_rental_management.dto.response.InspectionPickupResponse;
import fa.training.car_rental_management.entities.Booking;
import fa.training.car_rental_management.entities.Inspection;
import fa.training.car_rental_management.entities.InspectionPhoto;
import fa.training.car_rental_management.entities.Users;
import fa.training.car_rental_management.enums.BookingStatus;
import fa.training.car_rental_management.enums.InspectionType;
import fa.training.car_rental_management.exception.ResourceNotFoundException;
import fa.training.car_rental_management.repository.BookingRepository;
import fa.training.car_rental_management.repository.InspectionPhotoRepository;
import fa.training.car_rental_management.repository.InspectionRepository;
import fa.training.car_rental_management.repository.UserRepository;
import fa.training.car_rental_management.services.InspectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InspectionServiceImpl implements InspectionService {

    private final InspectionRepository inspectionRecordRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final InspectionPhotoRepository inspectionPhotoRepository;

//    @Override
//    public Inspection createInspection(Inspection inspection) {
//        log.info("Creating inspection for booking: {}", inspection.getBookingId());
//        return inspectionRecordRepository.save(inspection);
//    }

    @Override
    public Optional<Inspection> getInspectionById(Integer id) {
        log.info("Fetching inspection with id: {}", id);
        return inspectionRecordRepository.findById(id);
    }

    @Override
    public List<Inspection> getInspectionsByBookingId(Integer bookingId) {
        log.info("Fetching inspections for booking: {}", bookingId);
        return inspectionRecordRepository.findByBookingId(bookingId);
    }

    @Override
    public List<Inspection> getInspectionsByInspectorId(Integer inspectorId) {
        log.info("Fetching inspections by inspector: {}", inspectorId);
        return inspectionRecordRepository.findByInspectorId(inspectorId);
    }

    @Override
    public List<Inspection> getInspectionsByType(InspectionType type) {
        log.info("Fetching inspections with type: {}", type);
        return inspectionRecordRepository.findByType(type);
    }

    @Override
    public List<Inspection> getAllInspections() {
        log.info("Fetching all inspections");
        return inspectionRecordRepository.findAll();
    }

//    @Override
//    public Inspection updateInspection(Inspection inspection) {
//        log.info("Updating inspection with id: {}", inspection.getId());
//        return inspectionRecordRepository.save(inspection);
//    }

    @Override
    public void deleteInspection(Integer id) {
        log.info("Deleting inspection with id: {}", id);
        inspectionRecordRepository.deleteById(id);
    }

    @Override
    @Transactional
    public InspectionPickupResponse createPickupRecord(InspectionPickupRequest request, Integer inspectorId) {
        log.info("Starting createPickupRecord for bookingId: {} and inspectorId: {}", request.getBookingId(), inspectorId);

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.getBookingId()));

        Users inspector = userRepository.findById(inspectorId)
                .orElseThrow(() -> new ResourceNotFoundException("Inspector not found with id: " + inspectorId));

        if(inspectorId != booking.getCustomerId()) {
            throw new IllegalArgumentException("Inspector ID does not match the booking's assigned inspector");
        }

        Inspection savedRecord = inspectionRecordRepository.save(Inspection.builder()
                .bookingId(booking.getId())
                .inspectorId(inspector.getId())
                .comments(request.getComments())
                .type(InspectionType.PICKUP)
                .carStatus(fa.training.car_rental_management.enums.CarStatus.valueOf(request.getCarStatus()))
                .date(LocalDateTime.now())
                .build());

        List<String> photoPaths = request.getPhotoKeyPaths();
        log.info("Received {} photo paths for inspection record id: {}", photoPaths != null ? photoPaths.size() : 0, savedRecord.getId());
        if (photoPaths != null && !photoPaths.isEmpty()) {
            for (String path : photoPaths) {
                InspectionPhoto photo = InspectionPhoto.builder()
                        .inspectionId(savedRecord.getId())
                        .url(path)
                        .inspection(savedRecord)
                        .build();
                inspectionPhotoRepository.save(photo);
            }
        }
        log.info("Saved inspection record with id: {}", savedRecord.getId());
        booking.setStatus(BookingStatus.ACTIVE);
        bookingRepository.save(booking);
        log.info("Updated booking status to ACTIVE for bookingId: {}", booking.getId());

        return InspectionPickupResponse.builder()
                .id(savedRecord.getId())
                .bookingId(savedRecord.getBookingId())
                .inspectorId(savedRecord.getInspectorId())
                .inspectionType(savedRecord.getType().name())
                .carStatus(savedRecord.getCarStatus().name())
                .date(savedRecord.getDate().toString())
                .photoKeyPaths(photoPaths)
                .build();
    }
}

