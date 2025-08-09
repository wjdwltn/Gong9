package com.gg.gong9.notification.sms.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SmsResponse(
        String messageId,
        String to,
        String statusCode,
        String statusMessage
) {
}
