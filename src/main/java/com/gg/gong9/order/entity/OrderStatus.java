package com.gg.gong9.order.entity;

import lombok.Getter;

@Getter

public enum OrderStatus {
    PAYMENT_COMPLETED("결제 완료"),
    ORDER_CONFIRMED("주문 확정"),
    SHIPPING("배송 중"),
    SHIPPED("배송 완료"),
    CANCELLING("취소 요청 중"),
    CANCELLED("취소 완료");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
