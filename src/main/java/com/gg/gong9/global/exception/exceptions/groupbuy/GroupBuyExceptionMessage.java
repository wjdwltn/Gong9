package com.gg.gong9.global.exception.exceptions.groupbuy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GroupBuyExceptionMessage {
    NO_PERMISSION("해당 상품에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
    NOT_FOUND_GROUPBUY("해당 공동구매가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    INVALID_OPERATION("결제된 수량보다 적은 총 수량으로 수정할 수 없습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_ENDED("이미 종료된 공동구매는 취소할 수 없습니다.", HttpStatus.BAD_REQUEST),
    ONLY_SELLER_CAN_REGISTER("공구는 판매자의 경우만 등록할 수 있습니다.", HttpStatus.FORBIDDEN),
    INVALID_TOTAL_QUANTITY("결제된 수량보다 적은 총 수량으로 수정할 수 없습니다.", HttpStatus.BAD_REQUEST);



    private final String text;
    private final HttpStatus status;
}
