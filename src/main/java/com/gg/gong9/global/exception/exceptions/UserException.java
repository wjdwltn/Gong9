package com.gg.gong9.global.exception.exceptions;

import com.gg.gong9.global.exception.BaseException;
import com.gg.gong9.global.exception.ExceptionMessage;

public class UserException extends BaseException {
    public UserException(ExceptionMessage message) {
        super(message.getText(), message.getStatus());
    }
}