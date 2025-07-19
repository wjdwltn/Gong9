package com.gg.gong9.global.exception.exceptions.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum S3ExceptionMessage {
    PRODUCT_NOT_FOUND("해당 상품이 존재하지 않습니다.", HttpStatus.NOT_FOUND);

    private final String text;
    private final HttpStatus status;
}
