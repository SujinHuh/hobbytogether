package com.hobbytogther.infra.mail;


public interface EmailService {

    void sendEmail(EmailMessage emailMessage);
}
