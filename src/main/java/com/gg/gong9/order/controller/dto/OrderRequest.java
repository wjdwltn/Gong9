package com.gg.gong9.order.controller.dto;

import jakarta.validation.constraints.Positive;

public record OrderRequest(

        Long groupBuyId,

        @Positive(message = "주문 수량은 1 이상이어야 합니다.")
        int quantity
) {
}
