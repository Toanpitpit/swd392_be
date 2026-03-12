package fa.training.car_rental_management.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface UploadService {
    // Single file upload
    String uploadFile(MultipartFile file, String bucketName, String folderPath) throws IOException;

    // Multiple files upload
    List<String> uploadMultipleFiles(List<MultipartFile> files, String bucketName, String folderPath) throws IOException;

    // Video upload with validation
    String uploadVideo(MultipartFile videoFile, String bucketName, String folderPath) throws IOException;

    // Multiple videos upload
    List<String> uploadMultipleVideos(List<MultipartFile> videoFiles, String bucketName, String folderPath) throws IOException;

    // Delete file from S3
    void deleteFile(String bucketName, String fileKey);

    // Generate presigned URL for file access
    String generatePresignedUrl(String bucketName, String fileKey, int expirationMinutes);

    // Get file metadata
    boolean fileExists(String bucketName, String fileKey);
}

