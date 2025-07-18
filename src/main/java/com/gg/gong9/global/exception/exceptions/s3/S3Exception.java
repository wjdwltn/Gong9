package com.gg.gong9.global.exception.exceptions.s3;

import com.gg.gong9.global.exception.BaseException;

public class S3Exception extends BaseException {
    public S3Exception(S3ExceptionMessage message) {
        super(message.getText(), message.getStatus());
    }
}