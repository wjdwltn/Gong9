package com.gg.gong9.order.controller.dto;

import com.gg.gong9.order.entity.OrderStatus;

public record OrderUserInfo(
        Long orderId,
        String name,
        String phone,
        String address,
        int quantity,
        OrderStatus status

) {
}
