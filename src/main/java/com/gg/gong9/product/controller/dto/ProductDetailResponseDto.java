package com.gg.gong9.product.controller.dto;

import com.gg.gong9.product.entity.Product;
import com.gg.gong9.product.entity.ProductImg;

import java.util.List;
import java.util.stream.Collectors;

public record ProductDetailResponseDto(
        Long id,
        String productName,
        String description,
        int price,
        String category,
        List<String> productImageUrls
) {
    public static ProductDetailResponseDto from(Product product) {
        List<String> imageUrls = product.getProductImgs().stream()
                .map(ProductImg::getProductImageUrl)
                .collect(Collectors.toList());

        return new ProductDetailResponseDto(
                product.getId(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                imageUrls
        );
    }

}

