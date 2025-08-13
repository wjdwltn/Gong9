package com.gg.gong9.coupon.service;

import com.gg.gong9.global.exception.exceptions.coupon.CouponException;
import com.gg.gong9.global.exception.exceptions.coupon.CouponExceptionMessage;
import com.gg.gong9.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCouponService {

    private final StringRedisTemplate redisTemplate;

    private static final String LUA_SCRIPT = """
            local userKey = KEYS[1]
            local countKey = KEYS[2]

            if redis.call("EXISTS", userKey) == 1 then
                return 0
            end

            local count = tonumber(redis.call("GET", countKey))
            if count == nil or count <= 0 then
                return -1
            end

            redis.call("DECR", countKey)
            redis.call("SET", userKey, 1)
            redis.call("EXPIRE", userKey, 300)
            return 1
         """;

    private static final String USER_KEY_PATTERN = "coupon:%d:user:%d";
    private static final String COUNT_KEY_PATTERN = "coupon:%d:quantity";

    public void tryIssue(Long couponId, User user) {
        String userKey = buildUserKey(couponId, user.getId());
        String countKey = buildCountKey(couponId);

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(LUA_SCRIPT);
        script.setResultType(Long.class);

        Long result = redisTemplate.execute(script, List.of(userKey, countKey));

        if (result == null) {
            throw new IllegalStateException("Redis 실행 실패");
        }

        handleRedisResult(result.intValue());
    }

    private String buildUserKey(Long couponId, Long userId) {
        return String.format(USER_KEY_PATTERN, couponId, userId);
    }

    private String buildCountKey(Long couponId) {
        return String.format(COUNT_KEY_PATTERN, couponId);
    }

    private void handleRedisResult(int result) {
        switch (result) {
            case 1:
                return;
            case 0:
                throw new CouponException(CouponExceptionMessage.COUPON_ALREADY_ISSUED);
            case -1:
                throw new CouponException(CouponExceptionMessage.COUPON_OUT_OF_STOCK);
            default:
                throw new IllegalStateException("예상치 못한 Redis 반환값: " + result);
        }
    }
}


