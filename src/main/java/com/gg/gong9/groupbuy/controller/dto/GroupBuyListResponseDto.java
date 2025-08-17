package com.gg.gong9.groupbuy.controller.dto;

import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.groupbuy.entity.GroupBuy;

import java.time.LocalDateTime;

public record GroupBuyListResponseDto (
        Long id,
        String productName,
        int price,
        BuyStatus status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int totalQuantity,
        int limitQuantity,
        int currentStock,
        int joinedQuantity
) {
    public GroupBuyListResponseDto(GroupBuy groupBuy, int currentStock, int joinedQuantity){
        this(
                groupBuy.getId(),
                groupBuy.getProduct().getProductName(),
                groupBuy.getProduct().getPrice(),
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
