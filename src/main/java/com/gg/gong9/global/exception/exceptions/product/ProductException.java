package com.gg.gong9.global.exception.exceptions.product;

import com.gg.gong9.global.exception.BaseException;

public class ProductException extends BaseException {
    public ProductException(ProductExceptionMessage message) {
        super(message.getText(), message.getStatus());
    }
}
