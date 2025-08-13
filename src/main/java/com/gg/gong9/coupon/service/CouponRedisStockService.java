package com.gg.gong9.coupon.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponRedisStockService {

    private final StringRedisTemplate redisTemplate;

    // ===== 초기화 =====
    public void initializeStockAndUserOrders(Long couponId, int initialStock) {
        String stockKey = getStockKey(couponId);
        //String userOrderKey = getUserOrderKey(groupBuyId);

        redisTemplate.opsForValue().set(stockKey, String.valueOf(initialStock));
        //redisTemplate.delete(userOrderKey);
    }

    // ===== 조회 =====
//    public int getCurrentBuyerCount(Long couponId) {
//        String userOrderKey = getUserOrderKey(couponId);
//        Long count = redisTemplate.opsForSet().size(userOrderKey);
//        return count != null ? count.intValue() : 0;
//    }

    public int getCurrentStock(Long couponId) {
        String stockKey = getStockKey(couponId);
        String stock = redisTemplate.opsForValue().get(stockKey);
        return stock != null ? Integer.parseInt(stock) : 0;
    }

    public void deleteGroupBuyData(Long couponId) {
        String stockKey = getStockKey(couponId);
        //String userOrderKey = getUserOrderKey(groupBuyId);

        redisTemplate.delete(stockKey);
        //redisTemplate.delete(userOrderKey);
    }

    private String getStockKey(Long couponId){
        return "coupon:" + couponId + ":quantity";
    }

//    private String getUserOrderKey(Long couponId) {
//        return "coupon:" + couponId + ":userOrders";
//    }
}
