package com.gg.gong9.minibuy.controller.dto;

import com.gg.gong9.global.enums.Category;
import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.minibuy.entity.MiniBuy;

import java.time.LocalDateTime;

public record MiniBuyUrgentListResponseDto(
        Long id,
        String productName,
        String productImg,
        int price,
        Category category,
        BuyStatus status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int targetCount,
        int joinedCount
) {
    public static MiniBuyUrgentListResponseDto from(MiniBuy miniBuy) {
        return new MiniBuyUrgentListResponseDto(
                miniBuy.getId(),
                miniBuy.getProductName(),
                miniBuy.getProductImg(),
                miniBuy.getPrice(),
                miniBuy.getCategory(),
                miniBuy.getStatus(),
                miniBuy.getStartAt(),
                miniBuy.getEndAt(),
                miniBuy.getTargetCount(),
                miniBuy.getJoinedCount()
        );
    }
}
