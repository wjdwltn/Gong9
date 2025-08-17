package com.gg.gong9.groupbuy.controller.dto;

import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.global.enums.Category;
import com.gg.gong9.product.entity.Product;
import com.gg.gong9.product.entity.ProductImg;

import java.time.LocalDateTime;

public record GroupBuyUrgentListResponseDto(
        Long id,
        String productName,
        String productImage,
        int price,
        Category category,
        BuyStatus status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int totalQuantity,
        int currentStock,
        int joinedQuantity
) {
    public GroupBuyUrgentListResponseDto(GroupBuy groupBuy,int currentStock, int joinedQuantity){
        this(
                groupBuy.getId(),
                groupBuy.getProduct().getProductName(),
                extractFirstImageUrl(groupBuy.getProduct()),
                groupBuy.getProduct().getPrice(),
                groupBuy.getProduct().getCategory(),
                groupBuy.getStatus(),
                groupBuy.getStartAt(),
                groupBuy.getEndAt(),
                groupBuy.getTotalQuantity(),
                currentStock,
                joinedQuantity
        );
    }

    private static String extractFirstImageUrl(Product product) {
        return product.getProductImgs().stream()
                .map(ProductImg::getProductImageUrl)
                .findFirst()
                .orElse(null);
    }
}
