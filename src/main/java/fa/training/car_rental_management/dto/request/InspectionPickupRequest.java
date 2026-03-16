package fa.training.car_rental_management.dto.request;

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
public class InspectionPickupRequest {
    private Integer bookingId;
    private String comments;
    private String carStatus;
    private List<String> photoKeyPaths;
}
