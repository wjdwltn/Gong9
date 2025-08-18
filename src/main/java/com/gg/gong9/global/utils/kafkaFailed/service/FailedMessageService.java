package com.gg.gong9.global.utils.kafkaFailed.service;

import com.gg.gong9.global.utils.kafkaFailed.Repository.FailedMessageRepository;
import com.gg.gong9.global.utils.kafkaFailed.entity.FailedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FailedMessageService {

    private final FailedMessageRepository failedMessageRepository;

    public void saveFailedMessage(String payload, String topic, Exception ex) {
        FailedMessage failedMessage = new FailedMessage(
                topic,
                payload,
                ex.toString()
        );
        failedMessageRepository.save(failedMessage);
    }
}
