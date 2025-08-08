package com.gg.gong9.notification.sms.service;

import com.gg.gong9.notification.sms.controller.dto.SmsBulkMessage;
import com.gg.gong9.notification.sms.controller.dto.SmsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SolapiSmsService {

    @Value("${solapi.sender}")
    private String from;

    private static final String API_URL = "https://api.solapi.com/messages/v4/send";

    private final SolapiAuthService authService;
    private final SolapiBulkSmsSender solapiBulkSmsSender;
    private final RestTemplate restTemplate;

    public void sendSms(String to, String text) {
        HttpHeaders headers = authService.createHeaders();

        Map<String, Object> message = Map.of(
                "to", to,
                "from", from,
                "text", text
        );
        Map<String, Object> body = Map.of("message", message);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);
        // 응답 확인 및 예외 처리 로직 추가 가능
        System.out.println("[Solapi 응답] " + response.getStatusCode() + " / " + response.getBody());

    }

    public void sendBulkSms(List<SmsBulkMessage> messages) {
        if (messages.isEmpty()) {
            System.out.println("[Solapi] 벌크 SMS 발송 대상 없음");
            return;
        }

        List<Map<String, String>> messageList = messages.stream()
                .map(m -> Map.of(
                        "to",m.phoneNumber(),
                        "from",from,
                        "text", m.text()
                ))
                .toList();

        try {
            List<SmsResponse> responses = solapiBulkSmsSender.sendBulkSms(messageList);

//            for (SmsResponse response : responses) {
//                if (!"4000".equals(response.statusCode())) {
//                    log.error("문자 전송 실패 - 수신번호: {}, 상태코드: {}, 메시지: {}",
//                            response.to(), response.statusCode(), response.statusMessage());
//                } else {
//                    log.info("문자 전송 성공 - 수신번호: {}", response.to());
//                }
//            }

        } catch (IOException e) {
            log.error("Solapi 벌크 문자 전송 중 예외 발생", e);
            // 예외 처리, 재시도
        }
    }

}
