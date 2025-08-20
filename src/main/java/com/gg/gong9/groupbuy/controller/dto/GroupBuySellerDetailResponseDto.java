package com.gg.gong9.groupbuy.controller.dto;


import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.global.enums.Category;
import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.order.controller.dto.OrderUserInfo;
import com.gg.gong9.product.entity.Product;
import com.gg.gong9.product.entity.ProductImg;

import java.time.LocalDateTime;
import java.util.List;

public record GroupBuySellerDetailResponseDto(
        Long id,
        String productImages,
        String productName,
        int price,
        Category category,
        BuyStatus status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int totalQuantity,
        int limitQuantity,
        int currentStock,
        int joinedQuantity,
        List<OrderUserInfo> orderUsers,
        int totalOrderCount
){
    public static GroupBuySellerDetailResponseDto from(GroupBuy groupBuy, int currentStock, int joinedQuantity,List<OrderUserInfo> orderUsers){
        Product product = groupBuy.getProduct();

        String imageUrl = extractFirstImageUrl(product);

        return new GroupBuySellerDetailResponseDto(
                groupBuy.getId(),
                imageUrl,
                product.getProductName(),
                product.getPrice(),
                product.getCategory(),
                groupBuy.getStatus(),
                groupBuy.getStartAt(),
                groupBuy.getEndAt(),
                groupBuy.getTotalQuantity(),
                groupBuy.getLimitQuantity(),
                currentStock,
                joinedQuantity,
                orderUsers,
                orderUsers.size()
        );
    }

    private static String extractFirstImageUrl(Product product) {
        return product.getProductImgs().stream()
                .map(ProductImg::getProductImageUrl)
                .findFirst()
                .orElse(null);
    }

}
