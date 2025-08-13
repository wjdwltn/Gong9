package com.gg.gong9.coupon.kafka;

import com.gg.gong9.coupon.controller.dto.CouponIssuedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponProducer {
    private final KafkaTemplate<String, CouponIssuedEvent> kafkaTemplate;

    public void sendCouponIssuedEvent(CouponIssuedEvent event) {
        kafkaTemplate.send("coupon-issued", event);
    }
}

