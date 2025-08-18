package com.gg.gong9.coupon.controller.dto;

import com.gg.gong9.coupon.entity.CouponIssue;

import java.time.LocalDateTime;

public record CouponIssueListResponseDto(
        Long couponIssueId,
        Long couponId,
        String couponName,
        Long sellerId,
        int minOrderPrice,
        int discount,
        LocalDateTime starAt,
        LocalDateTime endAt,
        Long groupBuyId,
        String groupBuyName,
        String status
) {
    public static CouponIssueListResponseDto from(CouponIssue couponIssue) {
        return new CouponIssueListResponseDto(
                couponIssue.getId(),
                couponIssue.getCoupon().getId(),
                couponIssue.getCoupon().getName(),
                couponIssue.getCoupon().getUser().getId(),
                couponIssue.getCoupon().getMin_order_price(),
                couponIssue.getCoupon().getDiscount(),
                couponIssue.getCoupon().getStartAt(),
                couponIssue.getCoupon().getEndAt(),
                couponIssue.getCoupon().getGroupBuy().getId(),
                couponIssue.getCoupon().getGroupBuy().getProduct().getProductName(),
                couponIssue.getStatus().toString()
        );
    }
}
