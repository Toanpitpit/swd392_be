package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.config.VnpayConfig;
import fa.training.car_rental_management.dto.request.PaymentRequest;
import fa.training.car_rental_management.dto.response.PaymentVnpayResponse;
import fa.training.car_rental_management.entities.Booking;
import fa.training.car_rental_management.entities.Payment;
import fa.training.car_rental_management.enums.PaymentStatus;
import fa.training.car_rental_management.enums.PaymentType;
import fa.training.car_rental_management.exception.ResourceNotFoundException;
import fa.training.car_rental_management.repository.BookingRepository;
import fa.training.car_rental_management.repository.PaymentRepository;
import fa.training.car_rental_management.util.VnPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentVnpayService {

    private final VnpayConfig vnPayConfig;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    public PaymentVnpayResponse createVnPayPayment(PaymentRequest request ,String ipAddress) {

        long amount = 0;

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.getBookingId()));

        if(request.getPaymentType().equalsIgnoreCase("BOOKINGANDDEPOSIT")){
            if (booking.getStartTime().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Thanh toán quá hạn.");
            }

            Payment rentalFare = paymentRepository.findByBookingIdAndType(booking.getId(), PaymentType.RENTAL_FARE)
                    .filter(p -> p.getStatus() == PaymentStatus.PENDING)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment RENTAL_FARE not found or not in PENDING status for booking: " + booking.getId()));

            Payment deposit = paymentRepository.findByBookingIdAndType(booking.getId(), PaymentType.SECURITY_DEPOSIT)
                    .filter(p -> p.getStatus() == PaymentStatus.PENDING)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment SECURITY_DEPOSIT not found or not in PENDING status for booking: " + booking.getId()));

            amount = (long) ((rentalFare.getAmount() + deposit.getAmount()) * 100);
        } else {
            PaymentType type;
            try {
                type = PaymentType.valueOf(request.getPaymentType().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid payment type: " + request.getPaymentType());
            }


// Add logic if Refund in here
//            if(type == PaymentType.REFUND ){
//
//            }


            Payment payment = paymentRepository.findByBookingIdAndType(booking.getId(), type)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for type: " + type + " and booking: " + booking.getId()));

            amount = (long) (payment.getAmount() * 100);
        }

        String txnRef = request.getPaymentType() + "_" + request.getBookingId() + "_" + VnPayUtil.getRandomNumber(4);
        Map<String, String> vnpParams = vnPayConfig.getVNPayConfig(txnRef);

        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_IpAddr", ipAddress);

        String bankCode = request.getBankCode();
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
    @Transactional
    public PaymentVnpayResponse processCallback(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        String txnRef = request.getParameter("vnp_TxnRef");

        if (txnRef == null || txnRef.isEmpty()) {
            return PaymentVnpayResponse.builder()
                    .code("99")
                    .message("Invalid transaction reference")
                    .build();
        }

        String[] parts = txnRef.split("_");
        if (parts.length < 2) {
            return PaymentVnpayResponse.builder()
                    .code("99")
                    .message("Invalid transaction reference format")
                    .build();
        }

        String typeStr = parts[0];
        Integer bookingId = Integer.parseInt(parts[1]);

        if ("00".equals(status)) {
            if ("BOOKINGANDDEPOSIT".equalsIgnoreCase(typeStr)) {
                
                Booking booking = bookingRepository.findById(bookingId)
                        .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
                booking.setStatus(fa.training.car_rental_management.enums.BookingStatus.APPROVED);
                bookingRepository.save(booking);

                List<Payment> payments = paymentRepository.findByBookingId(bookingId);
                for (Payment payment : payments) {
                    if (payment.getType() == PaymentType.RENTAL_FARE || payment.getType() == PaymentType.SECURITY_DEPOSIT) {
                        payment.setStatus(PaymentStatus.COMPLETED);
                        paymentRepository.save(payment);
                    }
                }
            } else {
                PaymentType type = PaymentType.valueOf(typeStr.toUpperCase());
                Payment payment = paymentRepository.findByBookingIdAndType(bookingId, type)
                        .orElseThrow(() -> new ResourceNotFoundException("Payment not found for type: " + type + " and booking: " + bookingId));
                payment.setStatus(PaymentStatus.COMPLETED);
                paymentRepository.save(payment);

                // Finalize booking if this is a post-inspection payment
                Booking booking = bookingRepository.findById(bookingId)
                        .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
                if (booking.getStatus() == fa.training.car_rental_management.enums.BookingStatus.UNDER_INSPECTION) {
                    booking.setStatus(fa.training.car_rental_management.enums.BookingStatus.COMPLETED);
                    bookingRepository.save(booking);
                }
            }

            return PaymentVnpayResponse.builder()
                    .code("00")
                    .message("Thanh toán thành công")
                    .build();
        } else {
            if ("BOOKINGANDDEPOSIT".equalsIgnoreCase(typeStr)) {
                List<Payment> payments = paymentRepository.findByBookingId(bookingId);
                for (Payment payment : payments) {
                    if (payment.getType() == PaymentType.RENTAL_FARE || payment.getType() == PaymentType.SECURITY_DEPOSIT) {
                        payment.setStatus(PaymentStatus.FAILED);
                        paymentRepository.save(payment);
                    }
                }
            } else {
                PaymentType type = PaymentType.valueOf(typeStr.toUpperCase());
                Payment payment = paymentRepository.findByBookingIdAndType(bookingId, type)
                        .orElseThrow(() -> new ResourceNotFoundException("Payment not found for type: " + type + " and booking: " + bookingId));
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            }

            return PaymentVnpayResponse.builder()
                    .code(status)
                    .message("Thanh toán thất bại")
                    .build();
        }
    }
}