package com.hobbytogther.infra.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("local")  //언제 사용할 것 : local 에서 사용
@Component
public class ConsoleEmailService implements EmailService {
/** 아무런 빈을 주입 받을 필요가 없다. - 로깅만 하면됨 (어떤 메세지가 보내졌는지) */

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("sent email : {}",emailMessage.getMessage());
    }
}
