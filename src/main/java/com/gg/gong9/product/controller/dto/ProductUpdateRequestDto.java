package com.gg.gong9.product.controller.dto;

public record ProductUpdateRequestDto(
        String productName,
        String description,
        Integer price,
        String category
) {
}
