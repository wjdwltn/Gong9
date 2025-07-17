package com.gg.gong9.product.controller.dto;

import com.gg.gong9.category.entity.CategoryType;

public record ProductCreateRequestDto (
        String productName,
        String description,
        int price,
        CategoryType category
){
}
