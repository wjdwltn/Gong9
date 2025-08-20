package com.gg.gong9.notification.sms.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.gong9.notification.sms.controller.dto.SmsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic = "sms-topic";

    public void sendSmsMessage(SmsMessage smsMessage) {
        try {
            String json = objectMapper.writeValueAsString(smsMessage);
            kafkaTemplate.send(topic, String.valueOf(smsMessage.userId()), json)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Kafka 단건 SMS 메시지 발행 실패: {}", json, ex);
                        } else {
                            log.info("Kafka 단건 SMS 메시지 발행 성공: {}", json);
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("SMS 알림 Kafka 메시지 직렬화 실패", e);
            throw new RuntimeException("SMS 알림 Kafka 메시지 직렬화 실패", e);
        }
    }

    // 벌크 SMS Kafka 메시지 발행 (List<SmsMessage>)
    public void sendBulkMessages(List<SmsMessage> smsMessages) {
        if (smsMessages == null || smsMessages.isEmpty()) {
            log.warn("벌크 SMS 메시지 발행 대상이 없습니다.");
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(smsMessages);

            kafkaTemplate.send(topic, "bulk", json)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Kafka 벌크 SMS 메시지 발행 실패", ex);
                        } else {
                            log.info("Kafka 벌크 SMS 메시지 발행 성공 - count: {}", smsMessages.size());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("벌크 SMS 메시지 직렬화 실패", e);
            throw new RuntimeException("벌크 SMS 메시지 직렬화 실패", e);
        }
    }


}
