package com.hobbytogther.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Profile("dev") // 프로파일이 dev일 때
@Component
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        /**  html 이메일로 보내려면, 마임 메세로 보내야 한다.*/
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            // 첫전째 Parm - 마임 메세지 헬퍼로 감싸서 , 두번째 parm - 멀티파트 파일을 전송하는 마임메세지인지 여부 (이메일에 첨부파일을 담는다면, true) ,마지막 parm9 - 케릭터 셋
            mimeMessageHelper.setTo(emailMessage.getTo());
            mimeMessageHelper.setSubject(emailMessage.getSubject());
            mimeMessageHelper.setText(emailMessage.getMessage(), true); //html - true
            // 최종적으로 메세지를 보내주는 것
            javaMailSender.send(mimeMessage);
            log.info("sent email: {}", emailMessage.getMessage());
        } catch (MessagingException e) {
            log.error("failed to send email", e);
            throw  new RuntimeException(e);
        }
    }
}
