package com.gg.gong9.notification.sms.controller.dto;

import com.gg.gong9.notification.sms.controller.SmsNotificationType;

public record SmsMessage(
        Long userId,
        String userName,
        String phoneNumber,
        SmsNotificationType type
) {
}
