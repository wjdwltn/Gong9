package com.gg.gong9.global.utils.kafkaFailed.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "failed_messages")
public class FailedMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;          // 메시지가 온 토픽

    @Column(columnDefinition = "TEXT")
    private String payload;        // 실패 메시지 내용

    private String exception;      // 발생한 예외 클래스/메시지

    private LocalDateTime failedAt = LocalDateTime.now();

    public FailedMessage(String topic, String payload, String exception) {
        this.topic = topic;
        this.payload = payload;
        this.exception = exception;
        this.failedAt = LocalDateTime.now();
    }


}
