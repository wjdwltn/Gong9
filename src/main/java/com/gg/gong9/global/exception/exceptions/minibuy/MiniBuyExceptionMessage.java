package com.gg.gong9.global.exception.exceptions.minibuy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MiniBuyExceptionMessage {
    NO_PERMISSION_MINI_BUY("해당 소량공구에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
    INVALID_MINI_BUY_UPDATE_STATUS("모집취소 또는 모집완료 시 소량공구 수정이 불가능합니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_MINI_BUY("해당 소량공구가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    PARTICIPATION_FAIL("해당 소량공구 참여에 실패했습니다.", HttpStatus.BAD_REQUEST),
    CANNOT_INCREASE_REMAIN_COUNT("해당 소량공구 취소에 실패했습니다.", HttpStatus.BAD_REQUEST),
    CHAT_LINK_NOT_FOUND("해당 소량공구에 대한 채팅 링크가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    MINI_BUY_NOT_COMPLETED("현재 해당 소량 공구가 모집 완료 상태가 아닙니다.", HttpStatus.BAD_REQUEST),
    NOT_A_PARTICIPANT("해당 소량공구의 참여자가 아닙니다.", HttpStatus.BAD_REQUEST)
    ;


    private final String text;
    private final HttpStatus status;
}
