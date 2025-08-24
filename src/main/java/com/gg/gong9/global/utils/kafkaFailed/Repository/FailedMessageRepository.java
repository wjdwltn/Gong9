package com.gg.gong9.global.utils.kafkaFailed.Repository;

import com.gg.gong9.global.utils.kafkaFailed.entity.FailedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedMessageRepository extends JpaRepository<FailedMessage, Long> {
}
