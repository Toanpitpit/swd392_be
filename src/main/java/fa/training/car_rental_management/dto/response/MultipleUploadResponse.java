package fa.training.car_rental_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MultipleUploadResponse {
    private List<UploadResponse> uploadedFiles;
    private int totalFiles;
    private int successfulUploads;
    private String message;
}

