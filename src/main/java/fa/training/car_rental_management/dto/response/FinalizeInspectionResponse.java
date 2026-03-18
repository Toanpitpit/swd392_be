package fa.training.car_rental_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalizeInspectionResponse {
    private Integer bookingId;
    private String bookingStatus;
    private Double fineAmount;
    private String message;
}
