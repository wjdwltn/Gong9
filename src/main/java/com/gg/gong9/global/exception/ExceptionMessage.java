package com.gg.gong9.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {

    // ADMIN
    INVALID_TOKEN("유효하지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("만료된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_TOKEN("지원되지 않는 JWT 토큰입니다.", HttpStatus.BAD_REQUEST),
    EMPTY_CLAIMS("JWT 클레임이 비어있습니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ACTION("사용자가 이 작업을 수행할 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // USER
    USER_NOT_FOUND("해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_IS_PRESENT("해당 이메일로 가입된 계정이 이미 있습니다.", HttpStatus.CONFLICT),
    USER_NOT_LOGIN("로그인을 먼저 진행해주세요.", HttpStatus.UNAUTHORIZED),
    USER_LOGIN_FAIL("이메일과 비밀번호를 다시 확인해주세요.", HttpStatus.BAD_REQUEST),
    ALREADY_EXIST_EMAIL("이미 사용중인 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_EMAIL("이메일이 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    ADMIN_ACCESS_ONLY("관리자만 접근할 수 있습니다.", HttpStatus.FORBIDDEN),

    //S3
    S3_UPLOAD_FAILED("S3 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // PRODUCT
    PRODUCT_NOT_FOUND("해당 상품이 존재하지 않습니다.", HttpStatus.NOT_FOUND);

    ;

    private final String text;
    private final HttpStatus status;
}