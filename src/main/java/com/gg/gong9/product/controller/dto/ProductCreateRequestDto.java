package com.gg.gong9.product.controller.dto;

public record ProductCreateRequestDto (
        String productName,
        String description,
        int price,
        String category
){
}
