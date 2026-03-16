package fa.training.car_rental_management.dto.response;

import fa.training.car_rental_management.enums.FuelType;
import fa.training.car_rental_management.enums.TransmissionType;
import fa.training.car_rental_management.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDTO {
    private Integer id;
    private String vin;
    private String licensePlate;
    private String make;
    private String model;
    private Integer year;
    private TransmissionType transmissionType;
    private FuelType fuelType;
    private Integer seatCount;
    private String description;
    private VehicleStatus status;
    private Double basePrice;
    private Integer ownerId;
    private String city;
}

