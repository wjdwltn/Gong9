package com.gg.gong9.order.service;

import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.global.exception.exceptions.groupbuy.GroupBuyException;
import com.gg.gong9.global.exception.exceptions.groupbuy.GroupBuyExceptionMessage;
import com.gg.gong9.global.exception.exceptions.order.OrderException;
import com.gg.gong9.global.exception.exceptions.order.OrderExceptionMessage;
import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.groupbuy.repository.GroupBuyRepository;
import com.gg.gong9.notification.sms.controller.SmsNotificationType;
import com.gg.gong9.notification.sms.service.SmsNotificationService;
import com.gg.gong9.notification.sms.service.SmsService;
import com.gg.gong9.order.controller.dto.OrderCancelledEvent;
import com.gg.gong9.order.controller.dto.OrderDetailResponse;
import com.gg.gong9.order.controller.dto.OrderListResponse;
import com.gg.gong9.order.controller.dto.OrderRequest;
import com.gg.gong9.order.entity.Order;
import com.gg.gong9.order.entity.OrderStatus;
import com.gg.gong9.order.repository.OrderRepository;
import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.repository.UserRepository;
import com.gg.gong9.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.gg.gong9.global.exception.exceptions.groupbuy.GroupBuyExceptionMessage.NOT_FOUND_GROUP_BUY;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final GroupBuyRepository groupBuyRepository;
    private final OrderRedisStockService orderRedisStockService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Order createOrderTransaction(Long userId, OrderRequest request) {
        User user = userService.findByIdOrThrow(userId);

        GroupBuy groupBuy = groupBuyRepository.findById(request.groupBuyId())
                .orElseThrow(()->new GroupBuyException(GroupBuyExceptionMessage.NOT_FOUND_GROUP_BUY));

        existsByUserAndGroupBuy(user, groupBuy);

        groupBuy.decreaseRemainingQuantity(request.quantity());

        return createAndSaveOrder(user, groupBuy, request.quantity());
    }

    @Transactional
    public Order tryCreateOrderOnce(Long userId, OrderRequest request) {
        User user = userService.findByIdOrThrow(userId);

        GroupBuy groupBuy = groupBuyRepository.findById(request.groupBuyId())
                .orElseThrow(() -> new RuntimeException("GroupBuy not found"));

        existsByUserAndGroupBuy(user, groupBuy);

        groupBuy.decreaseRemainingQuantity(request.quantity());

        return createAndSaveOrder(user, groupBuy, request.quantity());
    }

    //주문 목록 조회(내가 주문한 목록)
    public List<OrderListResponse> getOrderList(Long userId){
        List<Order> orders = orderRepository.findAllByUserId(userId);
        return orders.stream()
                .map(OrderListResponse::from)
                .collect(Collectors.toList());
    }

    //주문 상세 조회
    public OrderDetailResponse getOrderDetail(Long userId, Long orderId){
        Order order = findByIdOrThrow(orderId);
        checkOwner(order,userId);
        return OrderDetailResponse.from(order);
    }

    //주문 취소
    @Transactional
    public void cancelOrder(Long userId, Long orderId){
        Order order = findByIdOrThrow(orderId);

        validateOrderCancelable(order, userId);

        order.cancel();

        GroupBuy groupBuy = order.getGroupBuy();
        groupBuy.increaseRemainingQuantity(order.getQuantity());

        //커밋 이후 취소 후처리(redis 재고 감소 및 취소 문자) 이벤트
        eventPublisher.publishEvent(new OrderCancelledEvent(userId, groupBuy.getId(), order.getQuantity()));
    }

    //주문 삭제(주문 목록에서)
    @Transactional
    public void deleteOrder(Long userId, Long orderId){
        Order order = findByIdOrThrow(orderId);
        checkOwner(order,userId);
        order.softDelete();
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateOrdersStatusBulk(Long groupBuyId) {
        orderRepository.updateStatusByGroupBuyId(
                OrderStatus.CANCELLED,
                groupBuyId,
                OrderStatus.PAYMENT_COMPLETED
        );
    }

    //검즘

    public GroupBuy validateAndGetGroupBuy(OrderRequest request) {
        GroupBuy groupBuy = groupBuyRepository.findById(request.groupBuyId())
                .orElseThrow(() -> new GroupBuyException(NOT_FOUND_GROUP_BUY));
        return groupBuy;
    }

    public Order createAndSaveOrder(User user, GroupBuy groupBuy, int quantity) {
        Order order = Order.builder()
                .quantity(quantity)
                .status(OrderStatus.PAYMENT_COMPLETED)
                .user(user)
                .groupBuy(groupBuy)
                .build();
        return orderRepository.save(order);
    }

    private void checkOwner(Order order, Long userId){
        if(!order.getUser().getId().equals(userId)){
            throw new OrderException(OrderExceptionMessage.ORDER_FORBIDDEN);
        }
    }

    private Order findByIdOrThrow(long id){
        return orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()-> new OrderException(OrderExceptionMessage.ORDER_NOT_FOUND));
    }

    public void existsByUserAndGroupBuy(User user, GroupBuy groupBuy){
        if(orderRepository.existsByUserAndGroupBuyAndStatusNot(user,groupBuy,OrderStatus.CANCELLED)){
            throw new OrderException(OrderExceptionMessage.DUPLICATE_ORDER);
        }
    }

    private void validateOrderCancelable(Order order, Long userId) {
        checkOwner(order, userId);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderException(OrderExceptionMessage.ORDER_ALREADY_CANCELLED);
        }

        if (order.getGroupBuy().getStatus() != BuyStatus.RECRUITING) {
            throw new OrderException(OrderExceptionMessage.ORDER_CANNOT_CANCEL);
        }
    }

    public List<User> findAllUsersByGroupBuy(Long groupBuyId){
        return orderRepository.findDistinctUsersByOrdersGroupBuyIdAndStatusNotCancelled(groupBuyId, OrderStatus.CANCELLED);
    }
}
