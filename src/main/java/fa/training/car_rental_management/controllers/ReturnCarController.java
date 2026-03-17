package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.dto.request.ReturnCarRequest;
import fa.training.car_rental_management.dto.response.ReturnCarResponse;
import fa.training.car_rental_management.services.ReturnCarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class ReturnCarController {

    private final ReturnCarService returnCarService;

    @PostMapping(value = "/{id}/return", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<String>>> returnCar(
            @PathVariable("id") Integer id,
            @ModelAttribute ReturnCarRequest request) {

        List<String> urls = returnCarService.returnCar(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Return car successfully", urls)
        );
    }
}