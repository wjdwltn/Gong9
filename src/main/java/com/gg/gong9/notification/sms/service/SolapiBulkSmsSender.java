package com.gg.gong9.notification.sms.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.gong9.notification.sms.controller.dto.SmsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.RequestBody;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolapiBulkSmsSender {

    private static final String BASE_URL = "https://api.solapi.com/messages/v4/groups";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final SolapiAuthService solapiAuthService;
    private final RestTemplate restTemplate;

    /**
     * 그룹 기반으로 대량 문자 발송
     */
    public List<SmsResponse> sendBulkSms(List<Map<String, String>> messages) throws IOException {
        String groupId = createMessageGroup();
        addMessagesToGroup(groupId, messages);
        sendGroupMessages(groupId);

        return getMessageGroupMessages(groupId);
    }

    /**
     * 그룹 생성
     */
    public String createMessageGroup() throws IOException {
        Map<String, Object> payload = Map.of(
                "appId", "aifvHy4yQe6E",
                "strict", false,
                "sdkVersion", "my-sdk-1.0",
                "osPlatform", "Windows",
                "customFields", Map.of(),
                "allowDuplicates", false
        );

        HttpHeaders headers = solapiAuthService.createHeaders();

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL, entity, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("그룹 생성 실패 - 응답 코드: {}, 응답 바디: {}", response.getStatusCodeValue(), response.getBody());
            throw new IOException("그룹 생성 실패 - 응답 코드: " + response.getStatusCodeValue());
        }

        String responseBody = response.getBody();

        Map<String, Object> jsonMap = objectMapper.readValue(responseBody, Map.class);
        String groupId = (String) jsonMap.get("groupId");
        log.info("그룹 생성 완료: groupId={}", groupId);

        return groupId;
    }

    /**
     * 메시지 추가
     */
    public void addMessagesToGroup(String groupId, List<Map<String, String>> messages) throws IOException {
        Map<String, Object> payload = Map.of("messages", messages);
        String json = objectMapper.writeValueAsString(payload);
        log.info("[메시지 추가 요청 바디] {}", json);

        HttpHeaders headers = solapiAuthService.createHeaders();
        String authorization = headers.getFirst("Authorization");

        Request request = new Request.Builder()
                .url(BASE_URL + "/" + groupId + "/messages")
                .put(RequestBody.create(json, JSON))
                .addHeader("Authorization", authorization)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = getResponseBodyOrThrow(response, "메시지 추가 실패");
            log.info("메시지 그룹에 메시지 추가 완료: {}", body);
        }
    }


    /**
     * 그룹 메시지 발송
     * -> 발송 후 응답 String 반환
     */
    public String sendGroupMessages(String groupId) throws IOException {
        log.info("[그룹메시지 발송 요청] groupId={}", groupId);

        HttpHeaders headers = solapiAuthService.createHeaders();

        Request request = new Request.Builder()
                .url(BASE_URL + "/" + groupId + "/send")
                .post(RequestBody.create("", JSON))
                .addHeader("Authorization", headers.getFirst("Authorization"))
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = getResponseBodyOrThrow(response, "메시지 전송 실패");
            log.info("그룹 메시지 발송 성공: {}", body);
            return body;
        }
    }

    public List<SmsResponse> getMessageGroupMessages(String groupId) throws IOException {
        HttpHeaders headers = solapiAuthService.createHeaders();

        Request request = new Request.Builder()
                .url(BASE_URL + "/" + groupId + "/messages")
                .get()
                .addHeader("Authorization", headers.getFirst("Authorization"))
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = getResponseBodyOrThrow(response, "그룹 메시지 개별 상태 조회 실패");
            log.info("[그룹 메시지 개별 상태 조회 성공] groupId={}, body={}", groupId, body);

            return parseMessageResponses(body);
        }
    }


    /**
     * 응답 바디 추출 및 실패 처리
     */
    private String getResponseBodyOrThrow(Response response, String errorMessage) throws IOException {
        ResponseBody responseBody = response.body();
        String bodyString = (responseBody != null) ? responseBody.string() : "";

        if (!response.isSuccessful()) {
            log.error("{} - 응답 코드: {}, 응답 바디: {}", errorMessage, response.code(), bodyString);
            throw new IOException(errorMessage + " - 응답 코드: " + response.code());
        }

        return bodyString;
    }

    /**
     * Solapi 그룹 메시지 발송 응답 JSON을 파싱하여 SmsResponse 리스트로 변환
     */
    private List<SmsResponse> parseMessageResponses(String responseBody) throws IOException {
        Map<String, Object> fullResponse = objectMapper.readValue(responseBody, Map.class);
        Object messageListObj = fullResponse.get("messageList");

        // Map<String, SmsResponse> 형태로 변환
        Map<String, SmsResponse> messageMap = objectMapper.convertValue(
                messageListObj,
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, SmsResponse.class)
        );

        // Map의 값들만 List로 만들어 반환
        return new ArrayList<>(messageMap.values());
    }
}
