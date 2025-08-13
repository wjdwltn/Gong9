package com.gg.gong9.coupon.controller.dto;

import com.gg.gong9.coupon.entity.Coupon;

import java.time.LocalDateTime;

public record CouponListResponseDto(
        Long id,
        String name,
        int quantity,
        int currentQuantity,
        int usedQuantity,
        boolean alreadyIssued,
        int minOrderPrice,
        int discount,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
    public CouponListResponseDto(Coupon coupon, int currentStock, int usedQuantity, boolean alreadyIssued) {
        this(
                coupon.getId(),
                coupon.getName(),
                coupon.getQuantity(),
                currentStock,
                usedQuantity,
                alreadyIssued,
                coupon.getMin_order_price(),
                coupon.getDiscount(),
                coupon.getStartAt(),
                coupon.getEndAt()
        );
    }
}
