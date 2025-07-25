package com.gg.gong9.notification.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService{

    private final JavaMailSender mailSender;

    public void sendEmail(String toEmail, String title, String content) {
        SimpleMailMessage message = createEmailForm(toEmail, title, content);
        try {
            mailSender.send(message);
        } catch (RuntimeException e) {
            log.debug("MailService.sendEmail exception occur toEmail: {}, " +
                    "title: {}, text: {}", toEmail, title, content);
            throw new RuntimeException("Unable to send email", e);
        }
    }

    private SimpleMailMessage createEmailForm(String toEmail,String title,String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(content);

        return message;
    }
}
