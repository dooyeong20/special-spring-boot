package com.megait.myhome.util;

import org.springframework.stereotype.Component;

public interface EmailService {
    void sendEmail(EmailMessage emailMessage);
}