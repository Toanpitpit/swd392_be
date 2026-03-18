package fa.training.car_rental_management.controllers;

import fa.training.car_rental_management.dto.request.PaymentRequest;
import fa.training.car_rental_management.dto.response.PaymentVnpayResponse;
import fa.training.car_rental_management.services.impl.PaymentVnpayService;
import fa.training.car_rental_management.util.VnPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class VnpayController {
    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;
    private final PaymentVnpayService paymentService;

    @PostMapping("/vn-pay")
    public ResponseEntity<PaymentVnpayResponse> pay(
            HttpServletRequest request,
            @RequestBody @Valid PaymentRequest dto) {
        String ipAddress = VnPayUtil.getIpAddress(request);

        PaymentVnpayResponse response = paymentService.createVnPayPayment( dto ,ipAddress );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<Void> payCallbackHandler(HttpServletRequest request) {
        PaymentVnpayResponse response = paymentService.processCallback(request);

        String txnRef = request.getParameter("vnp_TxnRef");
        String bookingId = "";
        if (txnRef != null && txnRef.contains("_")) {
            String[] parts = txnRef.split("_");
            if (parts.length >= 2) {
                bookingId = parts[1];
            }
        }
        if ("00".equals(response.code)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(frontendUrl + "/payment-success?bookingId=" + bookingId))
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(frontendUrl + "/manage-booking?paymentFailed=true"))
                    .build();
        }
    }
}