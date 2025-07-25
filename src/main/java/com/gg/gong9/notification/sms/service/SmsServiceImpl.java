package com.gg.gong9.notification.sms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SmsServiceImpl implements SmsService {

    @Value("${solapi.api-key}")
    private String apiKey;

    @Value("${solapi.api-secret}")
    private String apiSecret;

    @Value("${solapi.sender}")
    private String from;

    private static final String API_URL = "https://api.solapi.com/messages/v4/send";

//    public void sendStatusMessage(Order order) {
//        String message = generateMessageForStatus(order.getStatus(), order);
//        sendSms(order.getUser().getPhoneNumber(), message);
//    }
//
//    private String generateMessageForType(SmsNotificationType type) {
//    return switch (type) {
//        case GROUP_BUY_SUCCESS -> "🎉 공동구매 모집이 완료되었습니다!";
//        case GROUP_BUY_CANCELLED -> "❌ 공동구매가 취소되었습니다.";
//        case DELIVERY_STARTED -> "📦 상품이 발송되었습니다!";
//        case DELIVERY_COMPLETED -> "✅ 배송이 완료되었습니다. 감사합니다!";
//        case REFUND_COMPLETED -> "💸 환불이 완료되었습니다.";
//    };
//}

    public void sendMessage(String to, String text) {
        try {
            String date = ZonedDateTime.now(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

            String salt = generateSalt(16);

            String signatureData = date + salt;

            String signature = hmacSha256(signatureData, apiSecret);


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", String.format(
                    "HMAC-SHA256 apiKey=%s, date=%s, salt=%s, signature=%s",
                    apiKey, date, salt, signature
            ));

            Map<String, Object> message = Map.of(
                    "to", to,
                    "from", from,
                    "text", text
            );
            Map<String, Object> body = Map.of("message", message);
            // 7. 요청 보내기
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);
            System.out.println("[Solapi 응답] " + response.getStatusCode() + " / " + response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String hmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(rawHmac);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // 랜덤 salt 생성 메서드 (영숫자)
    private String generateSalt(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
