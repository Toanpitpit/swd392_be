package fa.training.car_rental_management.services;

import fa.training.car_rental_management.dto.request.ReturnCarRequest;
import fa.training.car_rental_management.dto.response.ReturnCarResponse;

import java.util.List;

public interface ReturnCarService {
    List<String> returnCar(Integer bookingId, ReturnCarRequest request);

}
