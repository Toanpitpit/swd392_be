package fa.training.car_rental_management.dto.request;

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
public class InspectionPickupRequest {
    private Integer bookingId;
    private Integer inspectorId;
    private String comments;
    private String carStatus;
}

