package com.gg.gong9.notification.sms.service;

import com.gg.gong9.notification.sms.controller.dto.SmsBulkMessage;
import com.gg.gong9.notification.sms.controller.dto.SmsMessage;
import com.gg.gong9.notification.sms.controller.SmsNotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    @Value("${solapi.sender}")
    private String from;

    private final MessageTemplateService templateService;
    private final SolapiSmsService solapiClient;

    @Override
    public void sendByType(SmsMessage smsMessage) {
        String message = templateService.generateMessageForType(smsMessage.type(),smsMessage.userName());
        solapiClient.sendSms(smsMessage.phoneNumber(), message);
    }

    @Override
    public void sendBulkByType(List<SmsMessage> messages) {
        if (messages.isEmpty()) return;

        SmsNotificationType type = messages.get(0).type();

        List<SmsBulkMessage> bulkMessages = messages.stream()
                .map(m -> new SmsBulkMessage(
                        m.phoneNumber(),
                        templateService.generateMessageForType(type, m.userName())
                ))
                .toList();

        solapiClient.sendBulkSms(bulkMessages);
    }

    private List<Map<String, String>> buildMessages(List<String> phoneNumbers) {
        return phoneNumbers.stream()
                .map(phone -> Map.of(
                        "to", phone,
                        "from", from,
                        "text", "공동구매가 마감되었습니다!"
                ))
                .toList();
    }

}
