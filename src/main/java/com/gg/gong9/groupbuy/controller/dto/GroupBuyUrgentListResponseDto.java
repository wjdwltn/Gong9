package com.gg.gong9.groupbuy.controller.dto;

import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.groupbuy.entity.Status;
import com.gg.gong9.product.entity.Category;

import java.time.LocalDateTime;

public record GroupBuyUrgentListResponseDto(
        Long id,
        String productName,
        int price,
        Category category,
        Status status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int totalQuantity,
        int joinedQuantity
) {
    public GroupBuyUrgentListResponseDto(GroupBuy groupBuy, int joinedQuantity){
        this(
                groupBuy.getId(),
                groupBuy.getProduct().getProductName(),
                groupBuy.getProduct().getPrice(),
                groupBuy.getProduct().getCategory(),
                groupBuy.getStatus(),
                groupBuy.getStartAt(),
                groupBuy.getEndAt(),
                groupBuy.getTotalQuantity(),
                joinedQuantity
        );
    }
}
