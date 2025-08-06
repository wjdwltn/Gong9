package com.gg.gong9.order.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.gong9.order.controller.dto.OrderKafkaMessage;
import com.gg.gong9.order.service.concurrency.strategy.OrderConcurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final OrderConcurrencyService orderConcurrencyService;

    @KafkaListener(topics = "order-topic", groupId = "order-group", concurrency = "3")
    public void listenOrderTopic(String message ,Acknowledgment ack) throws JsonProcessingException {
        try {
            OrderKafkaMessage orderMessage = objectMapper.readValue(message, OrderKafkaMessage.class);
            orderConcurrencyService.createOrderWithPess(orderMessage.userId(),orderMessage.toOrderRequest());
            log.info("주문 처리 시작 - userId: {}, groupBuyId: {}, quantity: {}",
                    orderMessage.userId(), orderMessage.groupBuyId(), orderMessage.quantity());
            // 수동 커밋
            ack.acknowledge();
        } catch (Exception e) {
            log.error("주문 처리 실패, 메시지: {}, 에러: {}", message, e.toString(), e);
            throw e; // 예외 던져서 errorHandler가 처리하도록
        }
    }
}
