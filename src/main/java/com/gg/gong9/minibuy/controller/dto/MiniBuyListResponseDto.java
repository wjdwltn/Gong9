package com.gg.gong9.minibuy.controller.dto;

import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.minibuy.entity.MiniBuy;

import java.time.LocalDateTime;

public record MiniBuyListResponseDto(
        Long id,
        String productName,
        String productImg,
        int price,
        BuyStatus status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int targetCount,
        int joinedCount
) {

    public static MiniBuyListResponseDto from(MiniBuy miniBuy) {
        return new MiniBuyListResponseDto(
                miniBuy.getId(),
                miniBuy.getProductName(),
                miniBuy.getProductImg(),
                miniBuy.getPrice(),
                miniBuy.getStatus(),
                miniBuy.getStartAt(),
                miniBuy.getEndAt(),
                miniBuy.getTargetCount(),
                miniBuy.getJoinedCount()
                );
    }

}
