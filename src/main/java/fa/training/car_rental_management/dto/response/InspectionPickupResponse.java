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
public class InspectionPickupResponse {
    private Integer id;
    private Integer bookingId;
    private Integer inspectorId;
    private String inspectionType;
    private String carStatus;
    private String date;
    private List<String> photoKeyPaths;
}

