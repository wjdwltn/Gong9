package com.gg.gong9.order.service.concurrency.redis_lau;

import com.gg.gong9.order.kafka.OrderKafkaProducer;
import com.gg.gong9.order.controller.dto.OrderRequest;
import com.gg.gong9.order.entity.Order;
import com.gg.gong9.order.service.OrderRedisStockService;
import com.gg.gong9.order.service.concurrency.strategy.OrderConcurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderLauScriptService {

    private final OrderConcurrencyService orderConcurrencyService;
    private final OrderRedisStockService orderRedisStockService;
    private final OrderKafkaProducer orderKafkaProducer;

    @Transactional
    public Order tryCreateOrderWithRedis(Long userId, OrderRequest request) {

        // Redis 재고 감소 시도
        boolean success = orderRedisStockService.decreaseStock(request.groupBuyId(), request.quantity());
        if (!success) {
            throw new RuntimeException("재고가 부족합니다.");
        }

        return orderConcurrencyService.createOrderWithPess(userId, request);
    }


    public void tryCreateOrderWithRedisWithKafka(Long userId, OrderRequest request) {

        // Redis lau-script 재고 감소
        boolean success = orderRedisStockService.decreaseStock(request.groupBuyId(), request.quantity());
        if (!success) {
            throw new RuntimeException("재고가 부족합니다.");
        }

        // Kafka 메시지 발행
        orderKafkaProducer.sendOrderMessage(userId, request);
    }
}
