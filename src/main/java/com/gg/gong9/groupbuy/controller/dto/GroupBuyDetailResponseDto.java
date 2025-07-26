package com.gg.gong9.groupbuy.controller.dto;


import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.global.enums.Category;
import com.gg.gong9.product.entity.Product;
import com.gg.gong9.product.entity.ProductImg;

import java.time.LocalDateTime;
import java.util.List;

public record GroupBuyDetailResponseDto(
        Long id,
        List<String> productImages,
        String productName,
        String description,
        int price,
        Category category,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int totalQuantity,
        int limitQuantity,
        int joinedQuantity
){
    public static GroupBuyDetailResponseDto from(GroupBuy groupBuy, int joinedQuantity){
        Product product = groupBuy.getProduct();

        List<String> imageUrls = extractImageUrls(product);

        return new GroupBuyDetailResponseDto(
                groupBuy.getId(),
                imageUrls,
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                groupBuy.getStartAt(),
                groupBuy.getEndAt(),
                groupBuy.getTotalQuantity(),
                groupBuy.getLimitQuantity(),
                joinedQuantity
        );
    }

    private static List<String> extractImageUrls(Product product) {
        return product.getProductImgs().stream()
                .map(ProductImg::getProductImageUrl)
                .toList();
    }

}
