package com.gg.gong9.groupbuy.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public record GroupBuyUpdateRequestDto(

        @PositiveOrZero(message = "총 수량은 0개 이상입니다.")
        int totalQuantity,

        @PositiveOrZero(message = "제한 수량은 0개 이상입니다.")
        int limitQuantity,

        @NotNull(message = "공구 시작일은 필수입니다.")
        LocalDateTime startAt,

        @NotNull(message = "공구 종료일은 필수입니다.")
        LocalDateTime endAt
) {
}
