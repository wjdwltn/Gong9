package com.gg.gong9.global.exception.exceptions.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthExceptionMessage {

    INVALID_TOKEN("유효하지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("만료된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_TOKEN("지원되지 않는 JWT 토큰입니다.", HttpStatus.BAD_REQUEST),
    EMPTY_CLAIMS("JWT 클레임이 비어있습니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ACTION("사용자가 이 작업을 수행할 권한이 없습니다.", HttpStatus.FORBIDDEN),

    EMAIL_NOT_FOUND("해당 이메일로 등록된 계정이 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_VERIFICATION_CODE("유효하지 않은 인증 코드입니다.", HttpStatus.BAD_REQUEST),
    VERIFICATION_CODE_NOT_MATCHED("인증 코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    ADMIN_ACCESS_ONLY("관리자만 접근할 수 있습니다.", HttpStatus.FORBIDDEN);

    private final String text;
    private final HttpStatus status;
}
