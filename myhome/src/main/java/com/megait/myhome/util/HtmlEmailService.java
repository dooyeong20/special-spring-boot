package com.megait.myhome.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Profile("dev")
@Service
@Slf4j
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService {
    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            messageHelper.setTo(emailMessage.getSubject());
            messageHelper.setSubject(emailMessage.getMessage());
            messageHelper.setText(emailMessage.getMessage(), true);

            javaMailSender.send(mimeMessage);

            log.info("Email has sent : {}", emailMessage.getMessage());
        } catch (MessagingException e){
            log.error("Failed to send email.", e);
        }
    }
}
