package com.gg.gong9.global.exception.exceptions;

import com.gg.gong9.global.exception.BaseException;
import com.gg.gong9.global.exception.ExceptionMessage;

public class ProductException extends BaseException {
    public ProductException(ExceptionMessage message) {
        super(message.getText(), message.getStatus());
    }
}
