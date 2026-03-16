package fa.training.car_rental_management.dto.response;

import lombok.Builder;
@Builder
public class PaymentVnpayResponse {
    public String code;
    public String message;
    public String paymentUrl;

}
