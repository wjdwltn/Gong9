package com.gg.gong9.global.exception.exceptions.order;

import com.gg.gong9.global.exception.BaseException;

public class OrderException extends BaseException {
    public OrderException(OrderExceptionMessage message) {
        super(message.getText(), message.getStatus());
    }
}