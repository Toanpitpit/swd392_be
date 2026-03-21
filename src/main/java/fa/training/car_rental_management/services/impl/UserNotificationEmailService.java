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

}

