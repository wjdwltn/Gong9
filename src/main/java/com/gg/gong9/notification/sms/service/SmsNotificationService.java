package com.gg.gong9.notification.sms.service;

import com.gg.gong9.notification.sms.controller.SmsNotificationType;
import com.gg.gong9.notification.sms.controller.dto.SmsMessage;
import com.gg.gong9.notification.sms.kafka.SmsKafkaProducer;
import com.gg.gong9.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsNotificationService {

    private final SmsKafkaProducer smsKafkaProducer;

    // 단일 사용자에게 문자 보내기
    public void sendSms(User user, SmsNotificationType type) {
        if (user == null || user.getPhoneNumber() == null) {
            log.warn("잘못된 사용자 정보: 문자 발송 대상이 없습니다.");
            return;
        }
        SmsMessage smsMessage = new SmsMessage(user.getId(), user.getUsername(), user.getPhoneNumber(), type);
        smsKafkaProducer.sendSmsMessage(smsMessage);
    }

    // 다수 사용자에게 문자 보내기 (벌크)
    public void sendBulkSms(List<User> users, SmsNotificationType type) {
        if (users == null || users.isEmpty()) {
            log.warn("문자 발송 대상 없음");
            return;
        }

        List<SmsMessage> bulkMessages = users.stream()
                .filter(u -> u != null && u.getPhoneNumber() != null)
                .map(user -> new SmsMessage(user.getId(), user.getUsername(), user.getPhoneNumber(), type))
                .toList();

        smsKafkaProducer.sendBulkMessages(bulkMessages);
    }
}
