package com.gg.gong9.coupon.controller.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public record CouponUpdateRequestDto(

        String name,

        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        int quantity,

        @Min(value = 1, message = "최소 주문 금액은 1원 이상이어야 합니다.")
        int minOrderPrice,

        @Min(value = 1, message = "할인 금액은 1원 이상이어야 합니다.")
        int discount,

        @Future(message = "시작 시간은 현재 시간 이후여야 합니다.")
        LocalDateTime startAt,

        LocalDateTime endAt
) {
}
