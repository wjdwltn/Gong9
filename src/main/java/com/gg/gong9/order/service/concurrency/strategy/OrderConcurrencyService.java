package com.gg.gong9.order.service.concurrency.strategy;

import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.global.exception.exceptions.groupbuy.GroupBuyException;
import com.gg.gong9.global.handler.RedissonHandler;
import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.groupbuy.repository.GroupBuyRepository;
import com.gg.gong9.notification.sms.service.SmsService;
import com.gg.gong9.order.controller.dto.OrderRequest;
import com.gg.gong9.order.controller.dto.OrderWithCompletion;
import com.gg.gong9.order.entity.Order;
import com.gg.gong9.order.service.OrderService;
import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.gg.gong9.global.exception.exceptions.groupbuy.GroupBuyExceptionMessage.NOT_FOUND_GROUP_BUY;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConcurrencyService {

    private final UserService userService;
    private final OrderService orderService;
    private final RedissonClient redissonClient;
    private final GroupBuyRepository gbRepository;
    private final RedissonHandler redissonHandler;
    private final SmsService smsService;

    //낙관적 락(기본)
    public Order createOrder_Opti(Long userId, OrderRequest request) {
        int maxRetry = 3;
        for (int attempt = 0; attempt < maxRetry; attempt++) {
            try {
                return orderService.tryCreateOrderOnce(userId, request);
            } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                log.warn("낙관적 락 충돌 발생, 재시도 시도: {}", attempt + 1);
                if (attempt == maxRetry - 1) throw e;
                try {
                    Thread.sleep(30); // 약간의 대기
                } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("주문 처리 실패");
    }

    //낙관적 락(재시도 개선)
    public Order createOrder_Opti2(Long userId, OrderRequest request) {
        int maxRetry = 5;
        long baseSleepMillis = 30;
        double multiplier = 2.0;

        for (int attempt = 0; attempt < maxRetry; attempt++) {
            try {
                return orderService.tryCreateOrderOnce(userId, request);
            } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                log.warn("낙관적 락 충돌 발생, 재시도 시도: {}", attempt + 1);
                if (attempt == maxRetry - 1) throw e;
                try {
                    // 백오프: 기본 대기시간 * (배수 ^ 시도횟수) ± 랜덤 0~10ms
                    long sleepTime = (long)(baseSleepMillis * Math.pow(multiplier, attempt));
                    sleepTime += (long)(Math.random() * 10); // 0~10ms 랜덤 추가
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("주문 처리 실패");
    }


    @PersistenceContext
    private EntityManager em;

    //비관적 락
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OrderWithCompletion createOrderWithPess(Long userId, OrderRequest request){

        em.clear(); // 1차 캐시 클리어

        User user = userService.findByIdOrThrow(userId);

        GroupBuy groupBuy = getGroupBuyWithLockOrThrow(request.groupBuyId());

        orderService.existsByUserAndGroupBuy(user, groupBuy); // 중복 주문 검사
        groupBuy.decreaseRemainingQuantity(request.quantity()); // db 재고 감소

        Order order = orderService.createAndSaveOrder(user, groupBuy, request.quantity());//주문 저장

        boolean groupBuyCompleted = false;
        List<User> allUsers = List.of();
        if (groupBuy.getRemainingQuantity() == 0 && groupBuy.getStatus() != BuyStatus.COMPLETED) {
            groupBuy.markAsCompleted();
            groupBuyCompleted = true;

            allUsers = orderService.findAllUsersByGroupBuy(groupBuy.getId());
        }

        return new OrderWithCompletion(order, groupBuyCompleted, allUsers);
    }

    //Redisson 분산락
    public Order createOrderWithRedisson(Long userId, OrderRequest request) {

        redissonHandler.lock(request.groupBuyId(),3,2);

        try{
            return orderService.createOrderTransaction(userId, request);
        } finally {
            redissonHandler.unlock(request.groupBuyId());
        }
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GroupBuy createOrderTransaction(User user, OrderRequest request){
        GroupBuy groupBuy = orderService.validateAndGetGroupBuy(request);
        orderService.existsByUserAndGroupBuy(user, groupBuy);
        groupBuy.decreaseRemainingQuantity(request.quantity());
        gbRepository.save(groupBuy);
        return groupBuy;
    }

    public GroupBuy getGroupBuyWithLock(Long groupBuyId) {
        return gbRepository.findByIdWithPessimisticLock(groupBuyId)
                .orElseThrow(() -> new GroupBuyException(NOT_FOUND_GROUP_BUY));
    }

    private GroupBuy getGroupBuyWithLockOrThrow(Long groupBuyId) {
        try {
            return getGroupBuyWithLock(groupBuyId);
        } catch (PessimisticLockingFailureException e) {
            throw new RuntimeException("락 획득 실패", e);
        }
    }

}
