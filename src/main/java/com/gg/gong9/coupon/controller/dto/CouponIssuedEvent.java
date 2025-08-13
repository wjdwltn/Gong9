package com.gg.gong9.coupon.controller.dto;

import java.io.Serializable;

public record CouponIssuedEvent(
        Long userId,
        Long couponId
) implements Serializable {
}

