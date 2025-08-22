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
            local userSetKey = KEYS[1]
            local countKey = KEYS[2]
            local userId = ARGV[1]

            if redis.call("SISMEMBER", userSetKey, userId) == 1 then
                return 0
            end

            local count = tonumber(redis.call("GET", countKey))
            if count == nil or count <= 0 then
                return -1
            end

            redis.call("DECR", countKey)
            redis.call("SADD", userSetKey, userId)
         
            return 1
         """;

    private static final String USER_SET_KEY_PATTERN = "coupon:%d:users";
    private static final String COUNT_KEY_PATTERN = "coupon:%d:quantity";

    private final DefaultRedisScript<Long> issueScript = new DefaultRedisScript<>() {{
        setScriptText(LUA_SCRIPT);
        setResultType(Long.class);
    }};

    // 쿠폰 발급 로직
    public void tryIssue(Long couponId, User user) {
        Long result = redisTemplate.execute(
                issueScript,
                List.of(buildUserSetKey(couponId), buildCountKey(couponId)),
                String.valueOf(user.getId())
        );

        handleRedisResult(result.intValue());
    }


    // 초기화
    public void initCouponStockInRedis(Long couponId, int quantity) {
        redisTemplate.delete(buildUserSetKey(couponId));
        redisTemplate.opsForValue().set(buildCountKey(couponId), String.valueOf(quantity));
    }

    // 조회
    public int getCurrentStock(Long couponId) {
        String stock = redisTemplate.opsForValue().get(buildCountKey(couponId));
        return stock != null ? Integer.parseInt(stock) : 0;
    }

    public int getCurrentIssueUserCount(Long couponId) {
        Long count = redisTemplate.opsForSet().size(buildUserSetKey(couponId));
        return count != null ? count.intValue() : 0;
    }

//    public boolean hasUserIssued(Long couponId, Long userId) {
//        Boolean exists = redisTemplate.opsForSet().isMember(buildUserSetKey(couponId), String.valueOf(userId));
//        return exists != null && exists;
//    }

    // 삭제
    public void deleteCouponKeys(Long couponId) {
        redisTemplate.delete(List.of(
                        buildCountKey(couponId),
                        buildUserSetKey(couponId)
                ));
        }

    private String buildUserSetKey(Long couponId) {
        return String.format(USER_SET_KEY_PATTERN, couponId);
    }

    private String buildCountKey(Long couponId) {
        return String.format(COUNT_KEY_PATTERN, couponId);
    }

    private boolean handleRedisResult(int result) {
        return switch (result) {
            case 1 -> true;
            case 0 -> throw new CouponException(CouponExceptionMessage.COUPON_ALREADY_ISSUED);
            case -1 -> throw new CouponException(CouponExceptionMessage.COUPON_OUT_OF_STOCK);
            default -> throw new IllegalStateException("예상치 못한 Redis 반환값: " + result);
        };

//        // 전 Lua 스크립트 (kafka적용 x) void 버전
//        private void handleRedisResult ( int result){
//            switch (result) {
//                case 1:
//                    return;
//                case 0:
//                    throw new CouponException(CouponExceptionMessage.COUPON_ALREADY_ISSUED);
//                case -1:
//                    throw new CouponException(CouponExceptionMessage.COUPON_OUT_OF_STOCK);
//                default:
//                    throw new IllegalStateException("예상치 못한 Redis 반환값: " + result);
//            };
        }


    }



