package fa.training.car_rental_management.services;

import fa.training.car_rental_management.dto.UploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface UploadService {

    UploadResponse uploadFile(MultipartFile file, String bucketName, String folderPath) throws IOException;

    List<UploadResponse> uploadMultipleFiles(List<MultipartFile> files, String bucketName, String folderPath) throws IOException;

    UploadResponse uploadVideo(MultipartFile videoFile, String bucketName, String folderPath) throws IOException;

    List<UploadResponse> uploadMultipleVideos(List<MultipartFile> videoFiles, String bucketName, String folderPath) throws IOException;

    void deleteFile(String bucketName, String fileKey);

    String generatePresignedUrl(String bucketName, String fileKey, int expirationMinutes);

    boolean fileExists(String bucketName, String fileKey);
}

