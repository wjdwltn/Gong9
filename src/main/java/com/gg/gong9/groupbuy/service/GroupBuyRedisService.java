package com.gg.gong9.groupbuy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupBuyRedisService {

    private final StringRedisTemplate redisTemplate;

    // ===== 초기화 =====
    public void initializeStockAndUserOrders(Long groupBuyId, int initialStock) {
        String stockKey = getStockKey(groupBuyId);
        String userOrderKey = getUserOrderKey(groupBuyId);

        redisTemplate.opsForValue().set(stockKey, String.valueOf(initialStock));
        redisTemplate.delete(userOrderKey);
    }

    // ===== 조회 =====
    public int getCurrentBuyerCount(Long groupBuyId) {
        String userOrderKey = getUserOrderKey(groupBuyId);
        Long count = redisTemplate.opsForSet().size(userOrderKey);
        return count != null ? count.intValue() : 0;
    }

    public int getCurrentStock(Long groupBuyId) {
        String stockKey = getStockKey(groupBuyId);
        String stock = redisTemplate.opsForValue().get(stockKey);
        return stock != null ? Integer.parseInt(stock) : 0;
    }

    public void deleteGroupBuyData(Long groupBuyId) {
        String stockKey = getStockKey(groupBuyId);
        String userOrderKey = getUserOrderKey(groupBuyId);

        redisTemplate.delete(stockKey);
        redisTemplate.delete(userOrderKey);
    }


    private String getStockKey(Long groupBuyId){
        return "groupBuy:" + groupBuyId + ":stock";
    }

    private String getUserOrderKey(Long groupBuyId) {
        return "groupBuy:" + groupBuyId + ":userOrders";
    }

}
