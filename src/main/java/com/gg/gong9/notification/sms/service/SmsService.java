package com.gg.gong9.notification.sms.service;

import com.gg.gong9.notification.sms.util.SmsNotificationType;
import com.gg.gong9.user.entity.User;

public interface SmsService {
    void sendByType(User user, SmsNotificationType type);
    void sendMessage(String to, String message);
}
