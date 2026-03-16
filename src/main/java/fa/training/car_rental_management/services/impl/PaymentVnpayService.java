package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.config.VnpayConfig;
import fa.training.car_rental_management.dto.response.PaymentVnpayResponse;
import fa.training.car_rental_management.util.VnPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentVnpayService {

    private final VnpayConfig vnPayConfig;

    public PaymentVnpayResponse createVnPayPayment(HttpServletRequest request) {

        long amount = Long.parseLong(request.getParameter("amount")) * 100;

        Map<String, String> vnpParams = vnPayConfig.getVNPayConfig();

        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_IpAddr", request.getRemoteAddr());

        String bankCode = request.getParameter("bankCode");
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParams.put("vnp_BankCode", bankCode);
        }

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext();) {

            String fieldName = itr.next();
            String fieldValue = vnpParams.get(fieldName);

            if (fieldValue != null && fieldValue.length() > 0) {

                String encodedName = URLEncoder.encode(fieldName, StandardCharsets.US_ASCII);
                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII);

                hashData.append(encodedName).append("=").append(encodedValue);
                query.append(encodedName).append("=").append(encodedValue);

                if (itr.hasNext()) {
                    hashData.append("&");
                    query.append("&");
                }
            }
        }

        String secureHash = VnPayUtil.hmacSHA512(
                vnPayConfig.getSecretKey(),
                hashData.toString()
        );

        query.append("&vnp_SecureHash=").append(secureHash);

        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + query;


        return PaymentVnpayResponse.builder()
                .code("00")
                .message("success")
                .paymentUrl(paymentUrl)
                .build();
    }
}