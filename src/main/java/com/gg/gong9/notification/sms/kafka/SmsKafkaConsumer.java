package com.gg.gong9.notification.sms.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.gong9.global.utils.kafkaFailed.service.FailedMessageService;
import com.gg.gong9.notification.sms.controller.dto.SmsMessage;
import com.gg.gong9.notification.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.springframework.kafka.retrytopic.TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final SmsService smsService;
    private final FailedMessageService failedMessageService;

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 10 * 1000, multiplier = 3, maxDelay = 10 * 60 * 1000),
            topicSuffixingStrategy = SUFFIX_WITH_INDEX_VALUE,
            dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR,
            include = { TimeoutException.class , IOException.class} //리트라이함
    )
    @KafkaListener(topics = "sms-topic", groupId = "sms-group", concurrency = "3",containerFactory = "kafkaListenerContainerFactory")
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
            throw e;
        }
    }

    @DltHandler
    public void handleFailedMessage(String failedMessage, Exception ex, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("DLT 메시지 도착, 재시도 모두 실패, 수동 처리 필요: {}", failedMessage, ex);

        failedMessageService.saveFailedMessage(failedMessage, topic, ex);
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