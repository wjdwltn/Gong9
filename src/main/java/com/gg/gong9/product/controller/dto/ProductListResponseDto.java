package com.gg.gong9.product.controller.dto;

import com.gg.gong9.product.entity.Product;
import com.gg.gong9.product.entity.ProductImg;

import java.util.List;
import java.util.stream.Collectors;

public record ProductListResponseDto(Long id,
                                     String productName,
                                     int price,
                                     List<String> productImageUrls

) {
    public ProductListResponseDto(Product product) {
        this(
                product.getId(),
                product.getProductName(),
                product.getPrice(),
                product.getProductImgs().stream()
                        .map(ProductImg::getProductImageUrl)
                        .collect(Collectors.toList())
        );
    }
}
