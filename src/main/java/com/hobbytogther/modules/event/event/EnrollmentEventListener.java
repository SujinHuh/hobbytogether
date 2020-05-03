package com.hobbytogther.modules.event.event;

import com.hobbytogther.infra.config.AppProperties;
import com.hobbytogther.infra.mail.EmailMessage;
import com.hobbytogther.infra.mail.EmailService;
import com.hobbytogther.modules.account.Account;
import com.hobbytogther.modules.event.Enrollment;
import com.hobbytogther.modules.event.Event;
import com.hobbytogther.modules.hobby.Hobby;
import com.hobbytogther.modules.notification.Notification;
import com.hobbytogther.modules.notification.NotificationRepository;
import com.hobbytogther.modules.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class EnrollmentEventListener {

    private final NotificationRepository notificationRepository;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    @EventListener
    public void handleEnrollmentEvent(EnrollmentEvent enrollmentEvent) {
        Enrollment enrollment = enrollmentEvent.getEnrollment();
        Account account = enrollment.getAccount();
        Event event = enrollment.getEvent();
        Hobby hobby = event.getHobby();

        if (account.isStudyEnrollmentResultByEmail()) {
            sendEmail(enrollmentEvent, account, event, hobby);
        }

        if (account.isStudyEnrollmentResultByWeb()) {
            createNotification(enrollmentEvent, account, event, hobby);
        }
    }

    private void sendEmail(EnrollmentEvent enrollmentEvent, Account account, Event event, Hobby hobby) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/hobby/" + hobby.getEncodedPath() + "/events/" + event.getId());
        context.setVariable("linkName", hobby.getTitle());
        context.setVariable("message", enrollmentEvent.getMessage());
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("hobbyTogether, " + event.getTitle() + " 모임 참가 신청 결과입니다.")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void createNotification(EnrollmentEvent enrollmentEvent, Account account, Event event, Hobby hobby) {
        Notification notification = new Notification();
        notification.setTitle(hobby.getTitle() + " / " + event.getTitle());
        notification.setLink("/hobby/" + hobby.getEncodedPath() + "/events/" + event.getId());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(enrollmentEvent.getMessage());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.EVENT_ENROLLMENT);
        notificationRepository.save(notification);
    }

}