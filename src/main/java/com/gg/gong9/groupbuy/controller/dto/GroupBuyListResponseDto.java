package com.gg.gong9.groupbuy.controller.dto;

import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.groupbuy.entity.GroupBuy;

import java.time.LocalDateTime;

public record GroupBuyListResponseDto (
        Long groupBuyId,
        String productName,
        int originalPrice,
        double discountRate,
        double discountedPrice,
        BuyStatus status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int totalQuantity,
        int limitQuantity,
        int currentStock,
        int joinedQuantity
) {
    public static GroupBuyListResponseDto from(GroupBuy groupBuy,int currentStock, int joinedQuantity) {
        int originalPrice = groupBuy.getProduct().getPrice();
        double discountedPrice = groupBuy.calculateDiscountedPrice(originalPrice, groupBuy.getDiscountRate());

        return new GroupBuyListResponseDto(
                groupBuy.getId(),
                groupBuy.getProduct().getProductName(),
                originalPrice,
                groupBuy.getDiscountRate(),
                discountedPrice,
                groupBuy.getStatus(),
                groupBuy.getStartAt(),
                groupBuy.getEndAt(),
                groupBuy.getTotalQuantity(),
                groupBuy.getLimitQuantity(),
                currentStock,
                joinedQuantity
        );
    }
}
