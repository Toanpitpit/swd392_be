package fa.training.car_rental_management.config;

import fa.training.car_rental_management.util.VnPayUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
public class VnpayConfig {

    @Getter
    @Value("${payment.vnPay.url}")
    private String vnp_PayUrl;

    @Value("${payment.vnPay.returnUrl}")
    private String vnp_ReturnUrl;

    @Value("${payment.vnPay.tmnCode}")
    private String vnp_TmnCode;

    @Getter
    @Value("${payment.vnPay.secretKey}")
    private String secretKey;

    @Value("${payment.vnPay.version}")
    private String vnp_Version;

    @Value("${payment.vnPay.command}")
    private String vnp_Command;

    @Value("${payment.vnPay.orderType}")
    private String orderType;

    public Map<String, String> getVNPayConfig(String txnRef) {

        Map<String, String> vnpParamsMap = new HashMap<>();

        vnpParamsMap.put("vnp_Version", vnp_Version);
        vnpParamsMap.put("vnp_Command", vnp_Command);
        vnpParamsMap.put("vnp_TmnCode", vnp_TmnCode);
        vnpParamsMap.put("vnp_CurrCode", "VND");

        // transaction id
        vnpParamsMap.put("vnp_TxnRef", txnRef);

        // order info
        vnpParamsMap.put("vnp_OrderInfo", "PaymentOrder" + txnRef);

        vnpParamsMap.put("vnp_OrderType", orderType);

        vnpParamsMap.put("vnp_Locale", "vn");

        // return url
        vnpParamsMap.put("vnp_ReturnUrl", vnp_ReturnUrl);

        // create date
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);

        // expire date
        calendar.add(Calendar.MINUTE, 15);
        String vnpExpireDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_ExpireDate", vnpExpireDate);

        return vnpParamsMap;
    }
}