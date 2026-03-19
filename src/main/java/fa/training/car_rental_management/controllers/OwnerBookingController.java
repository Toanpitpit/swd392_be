package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.ApiResponse;
import fa.training.car_rental_management.dto.request.ConfirmReturnRequest;
import fa.training.car_rental_management.dto.response.WaitingReturnResponse;
import fa.training.car_rental_management.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owner")
public class OwnerBookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/{ownerId}/waiting-return-confirm")
    public ResponseEntity<ApiResponse<List<WaitingReturnResponse>>> getWaitingReturnConfirm(
            @PathVariable Integer ownerId) {

        List<WaitingReturnResponse> bookings = bookingService.getWaitingReturnConfirm(ownerId);

        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách đơn chờ xác nhận trả xe thành công", bookings)
        );
    }
    @PostMapping("/booking/{bookingId}/confirm-return")
    public ResponseEntity<ApiResponse<String>> confirmReturn(
            @PathVariable Integer bookingId,
            @RequestBody ConfirmReturnRequest request) {

        bookingService.confirmReturn(bookingId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Xác nhận trả xe thành công", null)
        );
    }
}