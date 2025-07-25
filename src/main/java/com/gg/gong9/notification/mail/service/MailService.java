package com.gg.gong9.notification.mail.service;

public interface MailService {
    // void sendEmail(String to, String subject, String body);

    void sendEmail(String toEmail, String title, String content);


}
