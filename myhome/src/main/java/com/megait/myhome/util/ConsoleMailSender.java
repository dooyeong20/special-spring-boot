package com.megait.myhome.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Arrays;

@Profile("local")
@Component
@Slf4j
public class ConsoleMailSender implements JavaMailSender {
    @Override
    public MimeMessage createMimeMessage() {
        return null;
    }

    @Override
    public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
        return null;
    }

    @Override
    public void send(MimeMessage mimeMessage) throws MailException {

    }

    @Override
    public void send(MimeMessage... mimeMessages) throws MailException {

    }

    @Override
    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {

    }

    @Override
    public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {

    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        log.info("\n제목 : " + simpleMessage.getSubject()
                + "\n내용 : " + simpleMessage.getText()
                + "\n보낸 시간 : " + simpleMessage.getSentDate()
                + "\n송신인 : " + simpleMessage.getFrom()
                + "\n수신인 : " + Arrays.toString(simpleMessage.getTo()));
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {

    }
}
