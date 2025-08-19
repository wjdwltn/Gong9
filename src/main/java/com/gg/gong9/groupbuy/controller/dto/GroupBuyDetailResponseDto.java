package com.gg.gong9.groupbuy.controller.dto;


import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.global.enums.Category;
import com.gg.gong9.product.entity.Product;
import com.gg.gong9.product.entity.ProductImg;

import java.time.LocalDateTime;
import java.util.List;

public record GroupBuyDetailResponseDto(
        Long groupBuyId,
        List<String> productImages,
        String productName,
        String description,
        int originalPrice,
        double discountRate,
        double discountedPrice,
        Category category,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int totalQuantity,
        int limitQuantity,
        int currentStock,
        int joinedQuantity
) {
    public static GroupBuyDetailResponseDto from(GroupBuy groupBuy, int currentStock, int joinedQuantity) {
        Product product = groupBuy.getProduct();

        int originalPrice = product.getPrice();
        double discountedPrice = groupBuy.calculateDiscountedPrice(originalPrice, groupBuy.getDiscountRate());

        return new GroupBuyDetailResponseDto(
                groupBuy.getId(),
                extractImageUrls(product),
                product.getProductName(),
                product.getDescription(),
                originalPrice,
                groupBuy.getDiscountRate(),
                discountedPrice,
                product.getCategory(),
                groupBuy.getStartAt(),
                groupBuy.getEndAt(),
                groupBuy.getTotalQuantity(),
                groupBuy.getLimitQuantity(),
                currentStock,
                joinedQuantity
        );
    }

    private static List<String> extractImageUrls(Product product) {
        return product.getProductImgs().stream()
                .map(ProductImg::getProductImageUrl)
                .toList();
    }
}