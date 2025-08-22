package com.gg.gong9.groupbuy.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;


public record GroupBuyCreateRequestDto(
        @PositiveOrZero(message = "할인율은 0 이상입니다.")
        double discountRate,

        @NotNull(message = "총 수량은 필수입니다.")
        @Positive(message = "총 수량은 1 이상입니다.")
        int totalQuantity,

        @Positive(message = "1인 구매 제한 수량은 0 이상입니다.")
        int limitQuantity,

        @NotNull(message = "시작일 지정은 필수입니다.")
        LocalDateTime startAt,

        @NotNull(message = "종료일 지정은 필수입니다.")
        LocalDateTime endAt,

        @NotNull(message = "상품 정보는 필수입니다.")
        Long productId

) {


}
