package com.gg.gong9.groupbuy.controller.dto;

import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.global.enums.Category;

import java.time.LocalDateTime;

public record GroupBuyUrgentListResponseDto(
        Long id,
        String productName,
        int originalPrice,
        double discountRate,
        double discountedPrice,
        Category category,
        BuyStatus status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int totalQuantity,
        int currentStock,
        int joinedQuantity
) {
    public static GroupBuyUrgentListResponseDto from(GroupBuy groupBuy, int currentStock, int joinedQuantity) {
        int originalPrice = groupBuy.getProduct().getPrice();
        double discountedPrice = groupBuy.calculateDiscountedPrice(originalPrice, groupBuy.getDiscountRate());

        return new GroupBuyUrgentListResponseDto(
                groupBuy.getId(),
                groupBuy.getProduct().getProductName(),
                originalPrice,
                groupBuy.getDiscountRate(),
                discountedPrice,
                groupBuy.getProduct().getCategory(),
                groupBuy.getStatus(),
                groupBuy.getStartAt(),
                groupBuy.getEndAt(),
                groupBuy.getTotalQuantity(),
                currentStock,
                joinedQuantity
        );
    }
}