package com.gg.gong9.minibuy.controller.dto;

import com.gg.gong9.global.enums.Category;
import com.gg.gong9.minibuy.entity.MiniBuy;

import java.time.LocalDateTime;

public record MiniBuyDetailResponseDto (
        Long id,
        String productName,
        String productImg,
        String description,
        int price,
        Category category,
        int targetCount,
        LocalDateTime startAt,
        LocalDateTime endAt
){
    public static MiniBuyDetailResponseDto from(MiniBuy miniBuy) {
        return new MiniBuyDetailResponseDto(
                miniBuy.getId(),
                miniBuy.getProductName(),
                miniBuy.getProductImg(),
                miniBuy.getDescription(),
                miniBuy.getPrice(),
                miniBuy.getCategory(),
                miniBuy.getTargetCount(),
                miniBuy.getStartAt(),
                miniBuy.getEndAt()
        );
    }
}

