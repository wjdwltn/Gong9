package com.gg.gong9.global.exception.exceptions.coupon;

import com.gg.gong9.global.exception.BaseException;

public class CouponException extends BaseException {
    public CouponException(CouponExceptionMessage message) {
        super(message.getText(), message.getStatus());
    }
}
