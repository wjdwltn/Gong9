package com.gg.gong9.product.controller.dto;


import com.gg.gong9.category.entity.CategoryType;

public record ProductUpdateRequestDto(
        String productName,
        String description,
        Integer price,
        CategoryType category
) {
}
