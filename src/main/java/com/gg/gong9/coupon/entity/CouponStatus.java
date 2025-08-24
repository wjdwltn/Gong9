package com.gg.gong9.coupon.entity;

public enum CouponStatus {
    ACTIVE("발급가능"),
    EXPIRED("만료됨"),
    OUT_OF_STOCK("재고없음");

    private final String value;

    CouponStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
