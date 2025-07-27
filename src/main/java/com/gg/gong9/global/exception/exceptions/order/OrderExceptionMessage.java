package com.gg.gong9.global.exception.exceptions.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderExceptionMessage {

    ORDER_NOT_FOUND("해당 주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ORDER_FORBIDDEN("해당 주문에 접근할 수 없습니다.", HttpStatus.FORBIDDEN),
    ORDER_ALREADY_CANCELLED("이미 취소된 주문입니다.", HttpStatus.BAD_REQUEST),
    ORDER_CANNOT_CANCEL("공동구매 모집중일 때만 주문 취소가 가능합니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_ORDER("이미 해당 공동구매에 주문한 내역이 있습니다.", HttpStatus.BAD_REQUEST)
    ;

    private final String text;
    private final HttpStatus status;
}
