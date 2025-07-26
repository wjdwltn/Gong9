package com.gg.gong9.minibuy.controller.dto;

import com.gg.gong9.global.enums.Category;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public record MiniBuyUpdateRequestDto(

        String productName,

        String description,

        @PositiveOrZero(message = "가격은 0 이상입니다.")
        Integer price,

        Category category,

        @PositiveOrZero(message = "총 수량은 0개 이상입니다.")
        int targetCount,

        LocalDateTime startAt,

        LocalDateTime endAt
) {
}
