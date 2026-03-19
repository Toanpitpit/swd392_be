package fa.training.car_rental_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalizeInspectionRequest {
    private Integer bookingId;
    private String comments;
    private Double fineAmount;
}
