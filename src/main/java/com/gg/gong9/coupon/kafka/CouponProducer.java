package com.gg.gong9.coupon.kafka;

import com.gg.gong9.coupon.controller.dto.CouponIssuedEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CouponProducer {
    private final KafkaTemplate<String, CouponIssuedEvent> kafkaTemplate;


    public CouponProducer(@Qualifier("couponEventKafkaTemplate") KafkaTemplate<String, CouponIssuedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCouponIssuedEvent(CouponIssuedEvent event) {
        kafkaTemplate.send("coupon-issued", event);
    }
}

