package com.gg.gong9.order.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.gong9.order.controller.dto.OrderKafkaMessage;
import com.gg.gong9.order.controller.dto.OrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper; // Jackson

    private final String topic = "order-topic";

    public void sendOrderMessage(Long userId, OrderRequest request) {
        try {
            OrderKafkaMessage message = new OrderKafkaMessage(userId, request.groupBuyId(), request.quantity());
            String json = objectMapper.writeValueAsString(message);
            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(topic, String.valueOf(request.groupBuyId()), json);

            future.thenAccept(result -> {
                log.info("Kafka 메시지 발행 성공: {}", json);
            }).exceptionally(ex -> {
                log.error("Kafka 메시지 발행 실패", ex);
                return null;
            });
        } catch (JsonProcessingException e) {
            log.error("Kafka 메시지 직렬화 실패", e);
            throw new RuntimeException("Kafka 메시지 직렬화 실패", e);
        }
    }

}
