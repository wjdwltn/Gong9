package com.gg.gong9.notification.sms.controller.dto;

public record SmsResponse(
        String messageId,
        String to,
        String statusCode,
        String statusMessage
) {
}
