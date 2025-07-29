package com.gg.gong9.minibuy.controller.dto;

import com.gg.gong9.global.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public record MiniBuyCreateRequestDto (

        @NotBlank(message = "상품명은 필수입니다.")
        String productName,

        @NotBlank(message = "설명은 필수입니다.")
        String description,

        @PositiveOrZero(message = "가격은 0 이상입니다.")
        int price,

        @NotNull(message = "카테고리는 필수입니다.")
        Category category,

        @NotNull(message = "모집 인원은 필수입니다.")
        @Positive(message = "모집 인원은 1 이상입니다.")
        int targetCount,

        @NotNull(message = "시작일 지정은 필수입니다.")
        LocalDateTime startAt,

        @NotNull(message = "종료일 지정은 필수입니다.")
        LocalDateTime endAt

){

}
