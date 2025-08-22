package com.gg.gong9.order.controller.dto;

public record OrderKafkaMessage(
        Long userId,
        Long groupBuyId,
        int quantity,
        Long couponIssueId
) {
    public OrderRequest toOrderRequest() {
        return new OrderRequest(groupBuyId, quantity, couponIssueId);
    }
}