package com.gg.gong9.global.exception.exceptions.coupon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CouponExceptionMessage {
    COUPON_NOT_FOUND("해당 쿠폰이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    COUPON_ISSUE_NOT_FOUND("쿠폰 발급 내역이 없습니다.", HttpStatus.NOT_FOUND),
    COUPON_ALREADY_ISSUED("이미 발급된 쿠폰입니다.", HttpStatus.BAD_REQUEST),
    COUPON_EXPIRED("쿠폰이 만료되었습니다.", HttpStatus.BAD_REQUEST),
    COUPON_INVALID_STATUS("사용할 수 없는 쿠폰입니다.", HttpStatus.BAD_REQUEST),
    COUPON_NO_AUTHORITY("해당 쿠폰에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
    COUPON_OUT_OF_STOCK("쿠폰 재고가 부족합니다.", HttpStatus.BAD_REQUEST),
    COUPON_ALREADY_STARTED("이벤트 시작 이후 쿠폰은 수정이 불가능합니다.", HttpStatus.BAD_REQUEST),
    COUPON_DELETE_FORBIDDEN("이벤트 시작 이후 쿠폰은 쿠폰은 삭제가 불가능합니다.", HttpStatus.BAD_REQUEST),
    INVALID_END_TIME("종료 시간은 시작 시간보다 이후여야 합니다.", HttpStatus.BAD_REQUEST),
    COUPON_SOLD_OUT("쿠폰 재고가 모두 소진되었습니다.", HttpStatus.BAD_REQUEST),
    COUPON_LOCK_FAILED("쿠폰 발급을 위한 락을 획득하지 못했습니다.", HttpStatus.BAD_REQUEST);


    private final String text;
    private final HttpStatus status;
}
