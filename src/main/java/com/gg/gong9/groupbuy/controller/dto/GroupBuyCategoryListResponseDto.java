package com.gg.gong9.groupbuy.controller.dto;

import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.groupbuy.entity.GroupBuy;

import java.time.LocalDateTime;

public record GroupBuyCategoryListResponseDto(
        Long groupBuyId,
        String productName,
        int price,
        BuyStatus status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int totalQuantity,
        int joinedQuantity
) {
    public GroupBuyCategoryListResponseDto(GroupBuy groupBuy, int joinedQuantity) {
        this(
                groupBuy.getId(),
                groupBuy.getProduct().getProductName(),
                groupBuy.getProduct().getPrice(),
                groupBuy.getStatus(),
                groupBuy.getStartAt(),
                groupBuy.getEndAt(),
                groupBuy.getTotalQuantity(),
                joinedQuantity
        );
    }
}
