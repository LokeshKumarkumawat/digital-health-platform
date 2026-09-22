package com.digitalhealth.platform.notification.service;

import com.digitalhealth.platform.common.enums.NotificationType;
import com.digitalhealth.platform.notification.dto.NotificationCreateRequest;
import com.digitalhealth.platform.notification.entity.Notification;
import com.digitalhealth.platform.notification.repository.NotificationRepository;
import com.digitalhealth.platform.users.entity.User;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {


    private final NotificationRepository notificationRepo;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendEmail(NotificationCreateRequest request, User user) {

        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(request.getRecipient());
            helper.setSubject(request.getSubject());

            // Use template if provided
            if (request.getTemplateName() != null) {
                Context context = new Context();
                context.setVariables(request.getTemplateVariables());

                String htmlContent = templateEngine.process(
                        request.getTemplateName(),
                        context
                );

                helper.setText(htmlContent, true);
            } else {
                helper.setText(request.getMessage(), true);
            }

            mailSender.send(mimeMessage);

            log.info("Email sent successfully to={}, traceId={}",
                    request.getRecipient(),
                    MDC.get("traceId"));

            // Save notification
            Notification notification = Notification.builder()
                    .recipient(request.getRecipient())
                    .subject(request.getSubject())
                    .message(request.getMessage())
                    .type(request.getType())
                    .user(user)
                    .build();

            notificationRepo.save(notification);

        } catch (Exception e) {
            // keeping it SIMPLE as you asked
            log.error("Failed to send email, traceId={}", MDC.get("traceId"), e);
        }
    }
}