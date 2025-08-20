package com.gg.gong9.order.service.concurrency.jvm;

import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.groupbuy.repository.GroupBuyRepository;
import com.gg.gong9.notification.sms.service.SmsService;
import com.gg.gong9.order.controller.dto.OrderRequest;
import com.gg.gong9.order.entity.Order;
import com.gg.gong9.order.repository.OrderRepository;
import com.gg.gong9.order.service.OrderService;
import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderJvmService {

    private final UserService userService;
    private final GroupBuyRepository groupBuyRepository;
    private final OrderService orderService;


    private final ReentrantLock lock = new ReentrantLock();
    private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();


    //주문 생성
    //@Transactional
    public Order createOrder_Reen(Long userId, OrderRequest request){

        User user = userService.findByIdOrThrow(userId);

        GroupBuy groupBuy = null;

        lock.lock();
        try{
            groupBuy = orderService.validateAndGetGroupBuy(request);
            orderService.existsByUserAndGroupBuy(user, groupBuy);
            groupBuy.decreaseRemainingQuantity(request.quantity());
            groupBuyRepository.save(groupBuy);

        }finally{
            lock.unlock();
        }

        return orderService.createAndSaveOrder(user, groupBuy, request.quantity());
    }

    //주문 생성
    //@Transactional
    public Order createOrder_Reen_Map(Long userId, OrderRequest request){

        Long groupBuyId = request.groupBuyId();

        ReentrantLock lock = lockMap.computeIfAbsent(groupBuyId, id -> new ReentrantLock());

        User user = userService.findByIdOrThrow(userId);

        GroupBuy groupBuy = null;

        lock.lock();
        try{
            groupBuy = orderService.validateAndGetGroupBuy(request);
            orderService.existsByUserAndGroupBuy(user, groupBuy);
            groupBuy.decreaseRemainingQuantity(request.quantity());
            groupBuyRepository.save(groupBuy);

        }finally{
            lock.unlock();
        }

        return orderService.createAndSaveOrder(user, groupBuy, request.quantity());

    }

    //주문 생성
    public Order createOrder_syn(Long userId, OrderRequest request){

        User user = userService.findByIdOrThrow(userId);

        GroupBuy groupBuy = null;

        synchronized (this) {
            groupBuy = orderService.validateAndGetGroupBuy(request);
            orderService.existsByUserAndGroupBuy(user, groupBuy);
            groupBuy.decreaseRemainingQuantity(request.quantity());
            groupBuyRepository.save(groupBuy);
        }

        return orderService.createAndSaveOrder(user, groupBuy, request.quantity());
    }
}
