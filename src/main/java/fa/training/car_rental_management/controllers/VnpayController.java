package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.request.PaymentRequest;
import fa.training.car_rental_management.dto.response.PaymentVnpayResponse;
import fa.training.car_rental_management.services.impl.PaymentVnpayService;
import fa.training.car_rental_management.util.VnPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class VnpayController {

    private final PaymentVnpayService paymentService;

    @PostMapping("/vn-pay")
    public ResponseEntity<PaymentVnpayResponse> pay(
            HttpServletRequest request,
            @RequestBody @Valid PaymentRequest dto) {

        String ipAddress = VnPayUtil.getIpAddress(request);

        return ResponseEntity.ok(
                paymentService.createVnPayPayment(dto, ipAddress)
        );
    }



    @GetMapping("/vn-pay-callback")
    public PaymentVnpayResponse payCallbackHandler(HttpServletRequest request) {
        return paymentService.processCallback(request);
    }
}