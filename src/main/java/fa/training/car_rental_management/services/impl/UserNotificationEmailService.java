package fa.training.car_rental_management.services.impl;

import fa.training.car_rental_management.entities.Users;
import fa.training.car_rental_management.util.EmailHtmlUtils;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Service để gửi email notifications
 * Sử dụng generic method với templateVariables
 * Mỗi loại email sẽ có templateVariables khác nhau
 */
@Service
@RequiredArgsConstructor
public class UserNotificationEmailService {

    private static final Logger logger = LoggerFactory.getLogger(UserNotificationEmailService.class);

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.username}")
    private String senderEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * Gửi email sử dụng template Thymeleaf
     * Mỗi loại email có templateVariables khác nhau
     *
     * @param recipient         Người nhận
     * @param subject           Chủ đề
     * @param templateName      Tên file template (ví dụ: "booking-notification")
     * @param templateVariables Một Map chứa các biến (key) và giá trị (value) để chèn vào template
     */
    public void sendEmailWithTemplate(
            Users recipient,
            String subject,
            String templateName,
            Map<String, Object> templateVariables) {
        
        Context context = new Context();
        context.setVariables(templateVariables);

        try {
            String htmlContent = templateEngine.process(templateName, context);

            // Normalize images cho email
            htmlContent = EmailHtmlUtils.normalizeImgsForEmail(htmlContent);

            // Convert data:image → CID
            EmailHtmlUtils.ProcessResult processResult = EmailHtmlUtils.convertDataImagesToCid(htmlContent);
            String finalHtml = processResult.getProcessedHtml();

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(recipient.getEmail());
            helper.setSubject(subject);
            helper.setText(finalHtml, true);

            // Attach inline images từ TinyMCE
            for (Map.Entry<String, EmailHtmlUtils.InlineImage> entry : processResult.getInlineImages().entrySet()) {
                helper.addInline(
                        entry.getKey(),
                        new ByteArrayResource(entry.getValue().getBytes()),
                        entry.getValue().getMimeType()
                );
            }

            javaMailSender.send(message);
            logger.info("Email sent successfully to {}", recipient.getEmail());

        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", recipient.getEmail(), e.getMessage(), e);
        }
    }

    // ==================== Helper Methods để tạo templateVariables ====================

    /**
     * Tạo templateVariables cho booking accepted email
     */
    public Map<String, Object> buildBookingAcceptedVariables(
            Integer bookingId, String vehicleName, String startTime, 
            String endTime, Double totalPrice) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("bookingId", bookingId);
        vars.put("vehicleName", vehicleName);
        vars.put("startTime", startTime);
        vars.put("endTime", endTime);
        vars.put("totalPrice", totalPrice);
        vars.put("status", "Accepted");
        return vars;
    }

    /**
     * Tạo templateVariables cho booking rejected email
     */
    public Map<String, Object> buildBookingRejectedVariables(
            Integer bookingId, String vehicleName, String rejectionReason) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("bookingId", bookingId);
        vars.put("vehicleName", vehicleName);
        vars.put("rejectionReason", rejectionReason);
        vars.put("status", "Rejected");
        return vars;
    }

    /**
     * Tạo templateVariables cho vehicle return email
     */
    public Map<String, Object> buildVehicleReturnVariables(
            Integer bookingId, String vehicleName, String returnDateTime,
            Double depositAmount, String refundStatus) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("bookingId", bookingId);
        vars.put("vehicleName", vehicleName);
        vars.put("returnDateTime", returnDateTime);
        vars.put("depositAmount", depositAmount);
        vars.put("refundStatus", refundStatus);
        return vars;
    }

    /**
     * Tạo templateVariables cho payment completed email
     */
    public Map<String, Object> buildPaymentCompletedVariables(
            Integer bookingId, Double paymentAmount, String paymentMethod) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("bookingId", bookingId);
        vars.put("paymentAmount", paymentAmount);
        vars.put("paymentMethod", paymentMethod);
        return vars;
    }

    /**
     * Tạo templateVariables cho inspection completed email
     */
    public Map<String, Object> buildInspectionCompletedVariables(
            Integer bookingId, String inspectionStatus, String notes) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("bookingId", bookingId);
        vars.put("inspectionStatus", inspectionStatus);
        vars.put("notes", notes);
        return vars;
    }

    /**
     * Tạo templateVariables cho new booking request email (gửi cho car owner)
     */
    public Map<String, Object> buildNewBookingRequestVariables(
            Integer bookingId,
            String vehicleName,
            String ownerName,
            String startTime,
            String endTime,
            Integer totalDays,
            Double estimatedAmount,
            String customerName,
            String customerEmail,
            String customerPhone,
            String approveUrl,
            String rejectUrl) {
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("bookingId", bookingId);
        vars.put("vehicleName", vehicleName);
        vars.put("ownerName", ownerName);
        vars.put("startTime", startTime);
        vars.put("endTime", endTime);
        vars.put("totalDays", totalDays);
        vars.put("estimatedAmount", estimatedAmount);
        vars.put("customerName", customerName);
        vars.put("customerEmail", customerEmail);
        vars.put("customerPhone", customerPhone);
        vars.put("approveUrl", approveUrl);
        vars.put("rejectUrl", rejectUrl);
        return vars;
    }
}

