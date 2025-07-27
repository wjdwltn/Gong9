package com.gg.gong9.global.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
    데이터 바인딩 중 발생하는 에러 BindException 처리
    */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> bindException(BindException e) {
        String errorMsg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));


        ErrorResponse error = ErrorResponse.from(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), errorMsg);
        return ResponseEntity.badRequest().body(error);
    }

    /*
        @Valid 어노테이션을 사용한 DTO의 유효성 검사에서 예외가 발생한 경우
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = ErrorResponse.from(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), errorMsg);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Throwable rootCause = ex.getRootCause();

        if (rootCause != null && rootCause.getMessage() != null
                && rootCause.getMessage().toLowerCase().contains("duplicate")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ErrorResponse(409, "Conflict", "중복 주문이 발생했습니다."));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(500, "Internal Server Error", "서버 오류가 발생했습니다."));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        ErrorResponse error = ErrorResponse.from(
                e.getStatus().value(),
                e.getStatus().getReasonPhrase(),
                e.getMessage()
        );
        return ResponseEntity.status(e.getStatus()).body(error);
    }
}