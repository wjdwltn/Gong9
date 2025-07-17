package com.gg.gong9.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisVerificationCodeRepository implements VerificationCodeRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisVerificationCodeRepository(@Qualifier("customRedisTemplateString") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveCode(String key, String code, long ttlSeconds) {
        redisTemplate.opsForValue().set(key,code,ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Optional<String> getCode(String key) {
        String code = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(code);
    }

    @Override
    public void deleteCode(String key) {
        redisTemplate.delete(key);
    }
}
