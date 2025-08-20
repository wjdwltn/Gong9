package com.gg.gong9.order.controller.dto;

public record OrderCancelledEvent(
        Long userId,
        Long groupBuyId,
        int quantity
) {}
