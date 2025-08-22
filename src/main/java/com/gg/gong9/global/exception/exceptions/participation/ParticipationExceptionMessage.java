package com.gg.gong9.global.exception.exceptions.participation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ParticipationExceptionMessage {

    ALREADY_JOINED_PARTICIPATION("이미 해당 소량 공구게 참여 중 입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_PARTICIPATION("해당 참여 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ALREADY_CANCELED("이미 취소된 참여입니다.", HttpStatus.BAD_REQUEST),
    MINI_BUY_NOT_OPEN("현재 모집 중이 아닌 소량공구입니다.", HttpStatus.BAD_REQUEST),
    NOT_PERMISSION_PARTICIPATION("해당 소량공구 참여에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String text;
    private final HttpStatus status;
}
