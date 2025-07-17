package com.gg.gong9.global.exception.exceptions.user;

import com.gg.gong9.global.exception.BaseException;
import com.gg.gong9.global.exception.ExceptionMessage;

public class UserException extends BaseException {
    public UserException(UserExceptionMessage message) {
        super(message.getText(), message.getStatus());
    }
}