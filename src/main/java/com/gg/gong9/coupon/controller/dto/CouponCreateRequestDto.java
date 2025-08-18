package com.gg.gong9.coupon.controller.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CouponCreateRequestDto (

        @NotBlank(message = "쿠폰 이름은 비어 있을 수 없습니다.")
        String name,

        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        int quantity,

        @Min(value = 1, message = "최소 주문 금액은 1원 이상이어야 합니다.")
        int minOrderPrice,

        @Min(value = 1, message = "할인 금액은 1원 이상이어야 합니다.")
        int discount,

        @NotNull(message = "시작 시간은 필수입니다.")
        @Future(message = "시작 시간은 현재 시간 이후여야 합니다.")
        LocalDateTime startAt,

        @NotNull(message = "종료 시간은 필수입니다.")
        LocalDateTime endAt,

        @NotNull(message = "공동구매 선택은 필수입니다.")
        Long groupBuyId
) {
}
