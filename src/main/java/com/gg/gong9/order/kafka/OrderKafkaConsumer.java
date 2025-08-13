package com.gg.gong9.order.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.gong9.notification.sms.controller.dto.SmsMessage;
import com.gg.gong9.notification.sms.kafka.SmsKafkaProducer;
import com.gg.gong9.notification.sms.controller.SmsNotificationType;
import com.gg.gong9.notification.sms.service.SmsNotificationService;
import com.gg.gong9.order.controller.dto.OrderKafkaMessage;
import com.gg.gong9.order.controller.dto.OrderWithCompletion;
import com.gg.gong9.order.entity.Order;
import com.gg.gong9.order.service.OrderRedisStockService;
import com.gg.gong9.order.service.concurrency.strategy.OrderConcurrencyService;
import com.gg.gong9.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final OrderConcurrencyService orderConcurrencyService;
    private final SmsKafkaProducer smsKafkaProducer;
    private final OrderRedisStockService orderRedisStockService;
    private final SmsNotificationService smsNotificationService;

    @KafkaListener(topics = "order-topic", groupId = "order-group", concurrency = "3",containerFactory = "kafkaListenerContainerFactory")
    public void listenOrderTopic(String message ,Acknowledgment ack) throws JsonProcessingException {
        OrderKafkaMessage orderMessage = null;
        try {
            orderMessage = objectMapper.readValue(message, OrderKafkaMessage.class);
            OrderWithCompletion result = orderConcurrencyService.createOrderWithPess(orderMessage.userId(),orderMessage.toOrderRequest());
            log.info("주문 처리 시작 - userId: {}, groupBuyId: {}, quantity: {}",
                    orderMessage.userId(), orderMessage.groupBuyId(), orderMessage.quantity());

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
            throw e; // 예외 던져서 errorHandler가 처리
        }
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
