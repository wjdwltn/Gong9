package com.gg.gong9.order.controller.dto;

import com.gg.gong9.order.entity.Order;
import com.gg.gong9.order.entity.OrderStatus;
import com.gg.gong9.product.entity.Product;

import java.time.LocalDateTime;

public record OrderDetailResponse(
        Long orderId,
        Long productId,
        String productName,
        int productPrice,
        int quantity,
        OrderStatus orderStatus,
        GroupStatus groupStatus,
        LocalDateTime groupPurchaseStartAt,
        LocalDateTime groupPurchaseEndAt,
        LocalDateTime createdAt

) {
    public static OrderDetailResponse from(Order order) {
        GroupBuy groupBuy = order.getGroupBuy();
        Product product = groupBuy.getProduct();
        return new OrderListResponse(
                order.getId(),
                groupBuy.getId(),
                product.getProductName(),
                product.getPrice(),
                order.getQuantity(),
                order.getStatus(),
                groupBuy.getTotalQuantity(),
                groupBuy.getStatus(),
                groupBuy.getStartAt(),
                groupBuy.getEndAt(),
                order.getCreatedAt()
        );
    }
}
