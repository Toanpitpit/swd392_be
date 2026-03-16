package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.response.PaymentVnpayResponse;
import fa.training.car_rental_management.services.impl.PaymentVnpayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class VnpayController {

    private final PaymentVnpayService paymentService;

    @GetMapping("/vn-pay")
    public ResponseEntity<PaymentVnpayResponse> pay(HttpServletRequest request) {

        PaymentVnpayResponse response = paymentService.createVnPayPayment(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/vn-pay-callback")
    @ResponseBody
    public PaymentVnpayResponse payCallbackHandler(HttpServletRequest request) {

        System.out.println("===== CALLBACK HIT =====");

        request.getParameterMap().forEach((k,v)->{
            System.out.println(k + "=" + Arrays.toString(v));
        });

        String status = request.getParameter("vnp_ResponseCode");

        if ("00".equals(status)) {
            return PaymentVnpayResponse.builder()
                    .code("00")
                    .message("Thanh toán thành công")
                    .paymentUrl("")
                    .build();
        }

        return PaymentVnpayResponse.builder()
                .code(status)
                .message("Thanh toán thất bại")
                .paymentUrl("")
                .build();
    }
}