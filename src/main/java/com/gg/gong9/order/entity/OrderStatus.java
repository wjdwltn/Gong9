package com.gg.gong9.order.entity;

import lombok.Getter;


public enum OrderStatus {
    PAYMENT_COMPLETED,
    ORDER_CONFIRMED,
    SHIPPING,
    SHIPPED,
    CANCELLING,
    CANCELLED,
    ORDER_CANCELLED;
}
