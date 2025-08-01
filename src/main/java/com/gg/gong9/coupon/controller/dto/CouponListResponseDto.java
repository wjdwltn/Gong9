package com.gg.gong9.coupon.controller.dto;

import com.gg.gong9.coupon.entity.Coupon;

import java.time.LocalDateTime;

public record CouponListResponseDto(
        Long id,
        String name,
        int quantity,
        int remainQuantity,
        int minOrderPrice,
        int discount,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
    public static CouponListResponseDto from(Coupon coupon) {
        return new CouponListResponseDto(
                coupon.getId(),
                coupon.getName(),
                coupon.getQuantity(),
                coupon.getRemainQuantity(),
                coupon.getMin_order_price(),
                coupon.getDiscount(),
                coupon.getStartAt(),
                coupon.getEndAt()
        );
    }
}
