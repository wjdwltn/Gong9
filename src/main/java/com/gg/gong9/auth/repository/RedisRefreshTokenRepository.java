package com.gg.gong9.auth.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refreshToken:";
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 60 * 60 * 24 * 7; // 7일

    public RedisRefreshTokenRepository(@Qualifier("customRedisTemplateString") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String createRefreshTokenKey(String email) {
        return REFRESH_TOKEN_PREFIX + email;
    }

    @Override
    public void saveRefreshToken(String email, String refreshToken) {
        String key = createRefreshTokenKey(email);
        redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    @Override
    public Optional<String> getRefreshToken(String email) {
        String key = createRefreshTokenKey(email);
        String token = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(token);
    }

    @Override
    public void deleteRefreshToken(String email) {
        String key = createRefreshTokenKey(email);
        redisTemplate.delete(key);
    }
}
