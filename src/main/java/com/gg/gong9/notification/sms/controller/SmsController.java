package com.gg.gong9.notification.sms.controller;

import com.gg.gong9.notification.sms.service.SmsServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsController {

    private final SmsServiceImpl smsService;

    public SmsController(SmsServiceImpl smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public String sendSms(@RequestParam String to, @RequestParam String message) {
        smsService.sendMessage(to, message);
        return "문자 전송 완료!";
    }
}