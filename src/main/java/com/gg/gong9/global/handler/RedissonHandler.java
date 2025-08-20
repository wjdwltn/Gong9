package com.gg.gong9.global.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedissonHandler {

    private static final String GROUP_BUY_LOCK_KEY = "lock:groupbuy:";
    private final RedissonClient redissonClient;

    public void lock(Long key, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(GROUP_BUY_LOCK_KEY + key);
        try {
            boolean available = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (!available) {
                throw new IllegalStateException("Redisson 락 획득 실패: key=" + key);
            }
            log.info("Lock acquired for key: {}", GROUP_BUY_LOCK_KEY + key);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 획득 중 인터럽트 발생", e);
        }
    }

    public void unlock(Long key) {
        RLock lock = redissonClient.getLock(GROUP_BUY_LOCK_KEY + key);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            log.info("Lock released for key: {}", GROUP_BUY_LOCK_KEY + key);
        } else {
            log.warn("unlock 실패: currentThread가 락을 소유하지 않음 (key={})", key);
        }
    }



}
