package com.gg.gong9.global.exception.exceptions.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductExceptionMessage {
    PRODUCT_NOT_FOUND("해당 상품이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    PRODUCT_IMAGE_NOT_FOUND("해당 상품에 대한 이미지가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_ADMIN("관리자만 상품을 등록할 수 있습니다.", HttpStatus.FORBIDDEN),
    NO_PERMISSION("해당 상품에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN);



    private final String text;
    private final HttpStatus status;
}


