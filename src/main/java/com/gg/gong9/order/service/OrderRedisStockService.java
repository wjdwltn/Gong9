package com.gg.gong9.order.service;

import com.gg.gong9.global.exception.exceptions.order.OrderException;
import com.gg.gong9.global.exception.exceptions.order.OrderExceptionMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class OrderRedisStockService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> stockDecreaseWithDuplicateCheckScript;
    private final DefaultRedisScript<Long> restoreStockAndRemoveUserScript;

    public OrderRedisStockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        //재고 감소
        String decreaseLuaScript = """
            local stockKey = KEYS[1]
            local userOrderKey = KEYS[2]
            local userId = ARGV[1]
            local decrement = tonumber(ARGV[2])

            if redis.call('sismember', userOrderKey, userId) == 1 then
                return -2
            end

            local stock = tonumber(redis.call('get', stockKey) or '0')
            if stock < decrement then
                return -1
            end

            redis.call('decrby', stockKey, decrement)
            redis.call('sadd', userOrderKey, userId)

            return stock - decrement
            """;

        this.stockDecreaseWithDuplicateCheckScript = new DefaultRedisScript<>();
        this.stockDecreaseWithDuplicateCheckScript.setScriptText(decreaseLuaScript);
        this.stockDecreaseWithDuplicateCheckScript.setResultType(Long.class);

        // 재고 복구 + 중복주문 제거
        String restoreLuaScript = """
            redis.call('INCRBY', KEYS[1], ARGV[1])
            redis.call('SREM', KEYS[2], ARGV[2])
            return 1
            """;

        this.restoreStockAndRemoveUserScript = new DefaultRedisScript<>();
        this.restoreStockAndRemoveUserScript.setScriptText(restoreLuaScript);
        this.restoreStockAndRemoveUserScript.setResultType(Long.class);
    }

    public boolean decreaseStockWithDuplicateCheck(Long groupBuyId, Long userId, int quantity) {
        String stockKey = getStockKey(groupBuyId);
        String userOrderKey = getUserOrderKey(groupBuyId);

        Long result = redisTemplate.execute(
                stockDecreaseWithDuplicateCheckScript,
                Arrays.asList(stockKey, userOrderKey),
                String.valueOf(userId), String.valueOf(quantity)
        );

        if (result == null) {
            throw new RuntimeException("Redis 스크립트 실행 실패");
        }
        if (result == -2L) {
            throw new OrderException(OrderExceptionMessage.DUPLICATE_ORDER);
        }
        if (result == -1L) {
            throw new RuntimeException("재고가 부족합니다.");
        }
        return result >= 0;
    }

    //재고 복구
    public void increaseStockAndRemoveUserOrder(Long groupBuyId, Long userId, int quantity) {
        String stockKey = getStockKey(groupBuyId);
        String userOrderKey = getUserOrderKey(groupBuyId);

        redisTemplate.execute(
                restoreStockAndRemoveUserScript,
                Arrays.asList(stockKey, userOrderKey),
                String.valueOf(quantity),
                String.valueOf(userId)
        );
    }

    // 초기 세팅용
    public void initStock(Long groupBuyId, int stock) {
        redisTemplate.opsForValue().set("groupBuy:" + groupBuyId + ":stock", String.valueOf(stock));
    }

    private String getStockKey(Long groupBuyId) {
        return "groupBuy:" + groupBuyId + ":stock";
    }

    private String getUserOrderKey(Long groupBuyId) {
        return "groupBuy:" + groupBuyId + ":userOrders";
    }

}
