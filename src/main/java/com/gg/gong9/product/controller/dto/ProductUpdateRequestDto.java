package com.gg.gong9.product.controller.dto;


import com.gg.gong9.product.entity.Category;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductUpdateRequestDto(

        @NotNull(message = "상품명은 필수입니다.")
        String productName,

        @NotNull(message = "설명은 필수입니다.")
        String description,

        @NotNull(message = "가격은 필수입니다.")
        @PositiveOrZero(message = "가격은 0 이상입니다.")
        Integer price,

        @NotNull(message = "카테고리는 필수입니다.")
        Category category
) {
}
