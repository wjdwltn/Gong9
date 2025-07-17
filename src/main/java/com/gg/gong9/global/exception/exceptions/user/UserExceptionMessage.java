package com.gg.gong9.global.exception.exceptions.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserExceptionMessage {

    // USER
    USER_NOT_FOUND("해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_IS_PRESENT("해당 이메일로 가입된 계정이 이미 있습니다.", HttpStatus.CONFLICT),
    USER_NOT_LOGIN("로그인을 먼저 진행해주세요.", HttpStatus.UNAUTHORIZED),
    USER_LOGIN_FAIL("이메일과 비밀번호를 다시 확인해주세요.", HttpStatus.BAD_REQUEST),
    ALREADY_EXIST_EMAIL("이미 사용중인 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_EMAIL("이메일이 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    ADMIN_ACCESS_ONLY("관리자만 접근할 수 있습니다.", HttpStatus.FORBIDDEN),
    ;

    private final String text;
    private final HttpStatus status;

}
