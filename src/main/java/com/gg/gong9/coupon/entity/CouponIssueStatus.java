package com.gg.gong9.coupon.entity;

public enum CouponIssueStatus {
    UNUSED("미사용"),
    USED("사용됨"),
    EXPIRED("만료됨");

    private final String value;

    CouponIssueStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
