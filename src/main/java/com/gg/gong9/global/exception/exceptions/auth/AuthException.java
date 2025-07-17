package com.gg.gong9.global.exception.exceptions.auth;

import com.gg.gong9.global.exception.BaseException;
import com.gg.gong9.global.exception.ExceptionMessage;

public class AuthException extends BaseException {
    public AuthException(AuthExceptionMessage message) {
        super(message.getText(), message.getStatus());
    }
}