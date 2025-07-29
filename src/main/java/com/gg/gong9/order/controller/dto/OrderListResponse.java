package com.gg.gong9.order.controller.dto;

import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.order.entity.Order;
import com.gg.gong9.product.entity.Product;

import java.time.LocalDateTime;

public record OrderListResponse(
        Long orderId,
        int quantity,
        String orderStatus,
        LocalDateTime orderedAt,

        Long groupBuyId,
        String groupBuyStatus,
        LocalDateTime groupBuyStartAt,
        LocalDateTime groupBuyEndAt,

        Long productId,
        String productName,
        int productPrice,
        String productImgUrl

) {
    public static OrderListResponse from(Order order) {
        GroupBuy groupBuy = order.getGroupBuy();
        Product product = groupBuy.getProduct();

        String productImgUrl = product.getProductImgs().isEmpty() ? null
                : product.getProductImgs().get(0).getProductImageUrl();

        return new OrderListResponse(
                order.getId(),
                order.getQuantity(),
                order.getStatus().getValue(),
                order.getCreatedAt(),

                groupBuy.getId(),
                groupBuy.getStatus().name(),
                groupBuy.getStartAt(),
                groupBuy.getEndAt(),

                product.getId(),
                product.getProductName(),
                product.getPrice(),
                productImgUrl
        );
    }
}
