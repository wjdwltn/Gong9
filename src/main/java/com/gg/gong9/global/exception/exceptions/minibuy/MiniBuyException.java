package com.gg.gong9.global.exception.exceptions.minibuy;

import com.gg.gong9.global.exception.BaseException;

public class MiniBuyException extends BaseException {
    public MiniBuyException(MiniBuyExceptionMessage message) {
        super(message.getText(), message.getStatus());
    }
}
