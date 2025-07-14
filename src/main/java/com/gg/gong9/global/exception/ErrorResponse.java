package com.gg.gong9.global.exception;

public record ErrorResponse(
        int status,
        String title,
        String message
) {
    public static ErrorResponse from(int status, String title, String message) {
        return new ErrorResponse(status, title, message);
    }
}