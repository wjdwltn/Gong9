// KafkaConsumerConfig.java
package com.gg.gong9.global.config.kafka;

import com.gg.gong9.coupon.controller.dto.CouponIssuedEvent;
import com.gg.gong9.global.exception.BaseException;
import com.gg.gong9.global.exception.exceptions.coupon.CouponException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor; // <-- 추가
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.group-id}")
    private String groupId;

    private final KafkaTemplate<String, CouponIssuedEvent> kafkaTemplate;
    private final AsyncTaskExecutor kafkaConsumerExecutor; // <-- Executor -> AsyncTaskExecutor

    public KafkaConsumerConfig(KafkaTemplate<String, CouponIssuedEvent> kafkaTemplate,
                               AsyncTaskExecutor kafkaConsumerExecutor) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaConsumerExecutor = kafkaConsumerExecutor;
    }

    @Bean
    public ConsumerFactory<String, CouponIssuedEvent> consumerFactory() {
        final int maxPollRecords = 500;
        final int maxPollIntervalMs = 300_000;
        final int fetchMaxBytes = 5 * 1024 * 1024;
        final int maxPartitionFetchBytes = 1 * 1024 * 1024;

        Map<String, Object> configs = Map.ofEntries(
                Map.entry(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers),
                Map.entry(ConsumerConfig.GROUP_ID_CONFIG, groupId),
                Map.entry(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class),
                Map.entry(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class),
                Map.entry(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords),
                Map.entry(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs),
                Map.entry(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, fetchMaxBytes),
                Map.entry(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes)
        );

        JsonDeserializer<CouponIssuedEvent> jsonDeserializer =
                new JsonDeserializer<>(CouponIssuedEvent.class, false);
        jsonDeserializer.addTrustedPackages("com.gg.gong9.coupon.controller.dto");

        return new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CouponIssuedEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CouponIssuedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);

        factory.getContainerProperties().setListenerTaskExecutor(kafkaConsumerExecutor);

        // DLT
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + "-DLT", record.partition())
        );

        // 백오프
        ExponentialBackOff backOff = new ExponentialBackOff(500L, 2.0);
        backOff.setMaxElapsedTime(10_000L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        errorHandler.addNotRetryableExceptions(CouponException.class, BaseException.class);
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}
