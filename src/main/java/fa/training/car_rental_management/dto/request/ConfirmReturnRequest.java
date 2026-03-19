package fa.training.car_rental_management.dto.request;

import fa.training.car_rental_management.enums.CarStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmReturnRequest {

    private CarStatus carStatus;
    private Double fineAmount;
    private String comments;

}