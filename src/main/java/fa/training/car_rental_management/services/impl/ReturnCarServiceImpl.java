package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.dto.UploadResponse;
import fa.training.car_rental_management.dto.request.ReturnCarRequest;
import fa.training.car_rental_management.dto.response.ReturnCarResponse;
import fa.training.car_rental_management.entities.Booking;
import fa.training.car_rental_management.entities.Inspection;
import fa.training.car_rental_management.entities.InspectionPhoto;
import fa.training.car_rental_management.enums.BookingStatus;
import fa.training.car_rental_management.enums.CarStatus;
import fa.training.car_rental_management.enums.InspectionType;
import fa.training.car_rental_management.repository.BookingRepository;
import fa.training.car_rental_management.repository.InspectionPhotoRepository;
import fa.training.car_rental_management.repository.InspectionRepository;
import fa.training.car_rental_management.services.ReturnCarService;
import fa.training.car_rental_management.services.UploadService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReturnCarServiceImpl implements ReturnCarService {

    private final BookingRepository bookingRepository;
    private final InspectionRepository inspectionRepository;
    private final InspectionPhotoRepository photoRepository;
    private final UploadService uploadService;
    private final BookingServiceImpl bookingService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Override

    public List<String> returnCar(Integer bookingId, ReturnCarRequest request) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getStatus().equals(BookingStatus.ACTIVE)) {
            throw new RuntimeException("Booking is not ACTIVE");
        }

        // đổi trạng thái
        booking.setStatus(BookingStatus.UNDER_INSPECTION);
        bookingRepository.save(booking);

        // 🔥 gửi email cho chủ xe
        bookingService.sendReturnRequestEmailToOwner(booking);

        Integer ownerId = booking.getVehicle().getOwner().getId();

        Inspection inspection = new Inspection();
        inspection.setBookingId(booking.getId());
        inspection.setInspectorId(ownerId);
        inspection.setCarStatus(CarStatus.GOOD);
        inspection.setType(InspectionType.RETURN);
        inspection.setDate(LocalDateTime.now());

        inspectionRepository.save(inspection);

        List<String> photoUrls = new ArrayList<>();

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {

            try {

                List<UploadResponse> uploadedFiles =
                        uploadService.uploadMultipleFiles(
                                request.getFiles(),
                                bucketName,
                                "documents"
                        );

                for (UploadResponse file : uploadedFiles) {

                    InspectionPhoto photo = new InspectionPhoto();
                    photo.setInspectionId(inspection.getId());
                    photo.setUrl(file.getFileKey());
                    photoRepository.save(photo);

                    String url = uploadService.generatePresignedUrl(
                            bucketName,
                            file.getFileKey(),
                            60
                    );

                    photoUrls.add(url);
                }

            } catch (IOException e) {
                throw new RuntimeException("Upload photos failed");
            }
        }

        return photoUrls;
    }
}