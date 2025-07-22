package com.gg.gong9.order.controller.dto;

public record OrderRequest(

        Long groupBuyId,

        int quantity
) {
}
