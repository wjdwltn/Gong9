package com.gg.gong9.global.exception.exceptions;

import com.gg.gong9.global.exception.BaseException;
import com.gg.gong9.global.exception.ExceptionMessage;

public class S3Exception extends BaseException {
    public S3Exception(ExceptionMessage message) {
        super(message.getText(), message.getStatus());
    }
}