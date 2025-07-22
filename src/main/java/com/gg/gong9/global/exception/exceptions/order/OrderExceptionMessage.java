package com.gg.gong9.global.exception.exceptions.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderExceptionMessage {

    ORDER_NOT_FOUND("해당 주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ORDER_FORBIDDEN("해당 주문에 접근할 수 없습니다.", HttpStatus.FORBIDDEN)
    ;

    private final String text;
    private final HttpStatus status;
}
