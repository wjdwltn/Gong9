package com.gg.gong9.order.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.gong9.global.utils.kafkaFailed.service.FailedMessageService;
import com.gg.gong9.notification.sms.kafka.SmsKafkaProducer;
import com.gg.gong9.notification.sms.controller.SmsNotificationType;
import com.gg.gong9.notification.sms.service.SmsNotificationService;
import com.gg.gong9.order.controller.dto.OrderKafkaMessage;
import com.gg.gong9.order.controller.dto.OrderWithCompletion;
import com.gg.gong9.order.service.OrderRedisStockService;
import com.gg.gong9.order.service.concurrency.strategy.OrderConcurrencyService;
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
import java.util.concurrent.TimeoutException;

import static org.springframework.kafka.retrytopic.TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final OrderConcurrencyService orderConcurrencyService;
    private final SmsKafkaProducer smsKafkaProducer;
    private final OrderRedisStockService orderRedisStockService;
    private final SmsNotificationService smsNotificationService;
    private final FailedMessageService failedMessageService;

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 10 * 1000, multiplier = 3, maxDelay = 10 * 60 * 1000),
            topicSuffixingStrategy = SUFFIX_WITH_INDEX_VALUE,
            dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR,
            include = { TimeoutException.class , IOException.class} //리트라이함
    )
    @KafkaListener(topics = "order-topic", groupId = "order-group", concurrency = "3",containerFactory = "kafkaListenerContainerFactory")
    public void listenOrderTopic(String message ,Acknowledgment ack) throws IOException {
        OrderKafkaMessage orderMessage = null;
        try {
            orderMessage = objectMapper.readValue(message, OrderKafkaMessage.class);
            OrderWithCompletion result = orderConcurrencyService.createOrderWithPess(orderMessage.userId(),orderMessage.toOrderRequest());
            log.info("주문 처리 시작 - userId: {}, groupBuyId: {}, quantity: {}",
                    orderMessage.userId(), orderMessage.groupBuyId(), orderMessage.quantity());
            if (true) { // 테스트용 강제 예외
                throw new IOException("테스트용 강제 예외");
            }

            //주문 성공 메세지
            smsNotificationService.sendSms(result.order().getUser(),SmsNotificationType.ORDER_SUCCESS);

            //인원 모집 완료시 모든 주문자에게 공동구매 모집 완료 메세지(벌크처리)
            if(result.groupBuyCompleted()){
                log.info("인원 모집 완료,{}", objectMapper.writeValueAsString(result.allUsers()));
                smsNotificationService.sendBulkSms(result.allUsers(),SmsNotificationType.GROUP_BUY_SUCCESS);
            }

            ack.acknowledge();
            log.info("오프셋 커밋 완료 - offset processed");

        } catch (Exception e) {
            log.error("주문 처리 실패, 메시지: {}, 에러: {}", message, e.toString(), e);
            //주문 실패시 재고 복구
            if(orderMessage != null){
                rollbackRedisStock(orderMessage);
            } else {
                log.error("롤백 불가 - 주문 메시지 파싱 실패");
            }
            throw e;
        }
    }

    @DltHandler
    public void handleFailedMessage(String failedMessage, Exception ex, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("DLT 메시지 도착, 재시도 모두 실패, 수동 처리 필요: {}", failedMessage, ex);

        failedMessageService.saveFailedMessage(failedMessage, topic, ex);
    }

    private void rollbackRedisStock(OrderKafkaMessage orderMessage) {
        try {
            orderRedisStockService.increaseStockAndRemoveUserOrder(
                    orderMessage.groupBuyId(),
                    orderMessage.userId(),
                    orderMessage.quantity()
            );
            log.info("Redis 롤백 성공 - groupBuyId: {}, userId: {}, quantity: {}",
                    orderMessage.groupBuyId(), orderMessage.userId(), orderMessage.quantity());
        } catch (Exception redisEx) {
            log.error("Redis 롤백 실패, 에러: {}", redisEx.toString(), redisEx);
        }
    }
}
