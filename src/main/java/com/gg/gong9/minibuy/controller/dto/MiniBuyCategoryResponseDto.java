package com.gg.gong9.minibuy.controller.dto;

import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.minibuy.entity.MiniBuy;

import java.time.LocalDateTime;

public record MiniBuyCategoryResponseDto(
        Long miniBuyId,
        String productName,
        String productImg,
        int price,
        BuyStatus status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int targetCount,
        int joinedCount

){
    public static MiniBuyCategoryResponseDto from(MiniBuy miniBuy, int joinedCount) {
        return new MiniBuyCategoryResponseDto(
                miniBuy.getId(),
                miniBuy.getProductName(),
                miniBuy.getProductImg(),
                miniBuy.getPrice(),
                miniBuy.getStatus(),
                miniBuy.getStartAt(),
                miniBuy.getEndAt(),
                miniBuy.getTargetCount(),
                joinedCount
        );
    }
}
