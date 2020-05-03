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
import java.util.HashSet;
import java.util.Set;

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
                sendHobbyCreatedEmail(hobby, account,"새로운 Hobby가 생겼습니다",
                        "HOBBYTOGETHER, ' + hobby.getTitle() + ' Hobby 생겼습니다.");
            }

            if (account.isStudyCreatedByWeb()) {
                //notification
                createNotification(hobby, account,hobby.getShortDescription(),NotificationType.HOBBY_CREATED);
            }
        });
    }

    @EventListener
    public void handleHobbyUpdateEvent(HobbyUpdateEvent hobbyUpdateEvent) {
        //알림을 보낼 대상 Hobby관리자, 맴버
        Hobby hobby = hobbyRepository.findHobbyWithManagersAndMembersById(hobbyUpdateEvent.getHobby().getId());
        Set<Account> accounts = new HashSet<>();
        accounts.addAll(hobby.getManagers());
        accounts.addAll(hobby.getMembers());

        accounts.forEach(account ->{
            if(account.isStudyUpdatedByEmail()){
                //이메일
                sendHobbyCreatedEmail(hobby, account,hobbyUpdateEvent.getMessage(),
                        "HOBBYTOGETHER, ' + hobby.getTitle() + ' Hobby에 새소식이 있습니다.");
            }
            if(account.isStudyUpdatedByWeb()) {
                //웹으로
                 createNotification(hobby, account,hobbyUpdateEvent.getMessage(),NotificationType.HOBBY_UPDATED);
            }
        });
    }
    private void sendHobbyCreatedEmail(Hobby hobby, Account account,String contextMessage,String emailSubject ) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/hobby/" + hobby.getEncodedPath());
        context.setVariable("linkName", hobby.getTitle());
        context.setVariable("message",contextMessage);
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(emailSubject)
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void createNotification(Hobby hobby, Account account, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setTitle(hobby.getTitle());
        notification.setLink("/hobby/" + hobby.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(message);
        notification.setAccount(account);
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
    }
}

