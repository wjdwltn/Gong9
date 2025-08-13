package com.gg.gong9.notification.sms.service;

import com.gg.gong9.notification.sms.controller.dto.SmsMessage;

import java.util.List;

public interface SmsService {
    void sendByType(SmsMessage smsMessage);

    void sendBulkByType(List<SmsMessage> smsMessages);

}
