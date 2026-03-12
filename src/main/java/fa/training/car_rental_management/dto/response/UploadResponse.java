package fa.training.car_rental_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadResponse {
    private String fileName;
    private String fileUrl;
    private String fileKey;
    private long fileSize;
    private String contentType;
    private String message;
    private boolean success;
}

