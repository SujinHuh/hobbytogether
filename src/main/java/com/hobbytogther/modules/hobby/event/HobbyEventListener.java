package com.hobbytogther.modules.hobby.event;

import com.hobbytogther.infra.config.AppProperties;
import com.hobbytogther.infra.mail.EmailMessage;
import com.hobbytogther.infra.mail.EmailService;
import com.hobbytogther.modules.account.Account;
import com.hobbytogther.modules.account.AccountPredicates;
import com.hobbytogther.modules.account.AccountRepository;
import com.hobbytogther.modules.hobby.Hobby;
import com.hobbytogther.modules.hobby.validator.HobbyRepository;
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
public class HobbyEventListener {

    private final HobbyRepository hobbyRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;


    @EventListener /** 이밴트 처리 */
    public void handleHobbyCreatedEvent(HobbyCreatedEvent hobbyCreatedEvent) {
        /** Hobby 정보 조회 -> Tag ,Zone 에 맵핑되는 정보 조회*/
        // 이벤트를 처리할때 조회
        Hobby hobby = hobbyRepository.findHobbyWithTagsAndZonesById(hobbyCreatedEvent.getHobby().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(hobby.getTags(), hobby.getZones()));
        accounts.forEach(account -> {
            if (account.isStudyCreatedByEmail()) {
                //이메일 전송
                sendHobbyCreatedEmail(hobby, account);
            }

            if (account.isStudyCreatedByWeb()) {
                //notification
                saveHobbyCreatedNotification(hobby, account);
            }
        });
    }

    private void saveHobbyCreatedNotification(Hobby hobby, Account account) {
        Notification notification = new Notification();
        notification.setTitle(hobby.getTitle());
        notification.setLink("/hobby/" + hobby.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(hobby.getShortDescription());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.HOBBY_CREATED);
        notificationRepository.save(notification);
    }

    private void sendHobbyCreatedEmail(Hobby hobby, Account account) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/hobby/" + hobby.getEncodedPath());
        context.setVariable("linkName", hobby.getTitle());
        context.setVariable("message", "새로운 Hobby가 생겼습니다");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("HOBBYTOGETHER, '" + hobby.getTitle() + "' Hobby가 생겼습니다.")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }
}

