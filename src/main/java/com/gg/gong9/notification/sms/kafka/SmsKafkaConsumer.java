package com.gg.gong9.notification.sms.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.gong9.notification.sms.controller.dto.SmsMessage;
import com.gg.gong9.notification.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final SmsService smsService;

    @KafkaListener(topics = "sms-topic", groupId = "sms-group", concurrency = "3")
    public void listenSmsNotifyTopic(String message, Acknowledgment ack) throws JsonProcessingException {
        try {
            message = message.trim();
            if (message.startsWith("[")) {
                // 벌크 메시지 처리
                List<SmsMessage> bulkMessages = objectMapper.readValue(
                        message, new TypeReference<List<SmsMessage>>() {});
                sendBulkSms(bulkMessages);
            } else {
                // 단건 메시지 처리
                SmsMessage smsNotify = objectMapper.readValue(message, SmsMessage.class);
                sendSingleSms(smsNotify);
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("SMS 발송 처리 실패 - 메시지: {}, 에러: {}", message, e.toString(), e);
            throw e; // 예외 던져서 errorHandler가 처리
        }
    }

    private void sendSingleSms(SmsMessage sms){
        log.info("단건 SMS 발송 요청 - userId: {}, type: {}", sms.userId(), sms.type());
        smsService.sendByType(sms);
    }

    private void sendBulkSms(List<SmsMessage> messages){
        if(messages.isEmpty()){
            log.warn("벌크 SMS 발송 대상 없음");
            return;
        }
        log.info("벌크 SMS 발송 요청 - count: {}", messages.size());
        smsService.sendBulkByType(messages);
    }
}