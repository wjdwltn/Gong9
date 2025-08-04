package com.gg.gong9.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Component
public class OrderRedisStockService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> stockDecreaseScript;

    public OrderRedisStockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        String luaScript = """
            local stock = tonumber(redis.call('get', KEYS[1]) or '0')
            local decrement = tonumber(ARGV[1])
            if stock < decrement then
                return -1
            end
            redis.call('decrby', KEYS[1], decrement)
            return stock - decrement
            """;

        this.stockDecreaseScript = new DefaultRedisScript<>();
        this.stockDecreaseScript.setScriptText(luaScript);
        this.stockDecreaseScript.setResultType(Long.class);
    }

    public boolean decreaseStock(Long groupBuyId, int quantity) {
        String stockKey = "groupBuy:" + groupBuyId + ":stock";
        Long result = redisTemplate.execute(
                stockDecreaseScript,
                Collections.singletonList(stockKey),
                String.valueOf(quantity)
        );
        return result != null && result >= 0;
    }

    // 초기 세팅용
    public void initStock(Long groupBuyId, int stock) {
        redisTemplate.opsForValue().set("groupBuy:" + groupBuyId + ":stock", String.valueOf(stock));
    }

}
