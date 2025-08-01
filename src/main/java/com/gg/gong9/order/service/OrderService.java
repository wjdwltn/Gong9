package com.gg.gong9.order.service;

import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.global.exception.exceptions.groupbuy.GroupBuyException;
import com.gg.gong9.global.exception.exceptions.groupbuy.GroupBuyExceptionMessage;
import com.gg.gong9.global.exception.exceptions.order.OrderException;
import com.gg.gong9.global.exception.exceptions.order.OrderExceptionMessage;
import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.groupbuy.repository.GroupBuyRepository;
import com.gg.gong9.groupbuy.service.GroupBuyService;
import com.gg.gong9.notification.sms.service.SmsService;
import com.gg.gong9.notification.sms.util.SmsNotificationType;
import com.gg.gong9.order.controller.dto.OrderDetailResponse;
import com.gg.gong9.order.controller.dto.OrderListResponse;
import com.gg.gong9.order.controller.dto.OrderRequest;
import com.gg.gong9.order.entity.Order;
import com.gg.gong9.order.entity.OrderStatus;
import com.gg.gong9.order.repository.OrderRepository;
import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final GroupBuyService groupBuyService;
    private final GroupBuyRepository groupBuyRepository;
    private final SmsService smsService;

    //주문 생성
    public Order createOrder(Long userId, OrderRequest request){

        User user = userService.findByIdOrThrow(userId);

        GroupBuy groupBuy = groupBuyRepository.findById(request.groupBuyId())
                .orElseThrow(()->new GroupBuyException(GroupBuyExceptionMessage.NOT_FOUND_GROUP_BUY));

        //주문 중복 검증
        existsByUserAndGroupBuy(user,groupBuy);

        //추후 동시성 문제 고려
        //인원 확인 후 인원이 다 모이면 공구 모집 완료 메세지 구현

        Order order = Order.builder()
                .quantity(request.quantity())
                .status(OrderStatus.PAYMENT_COMPLETED)
                .user(user)
                .groupBuy(groupBuy)
                .build();

        Order savedOrder = orderRepository.save(order);

        //주문 성공 메세지
        smsService.sendByType(user, SmsNotificationType.ORDER_SUCCESS);

        return savedOrder;
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

        //추후 수량 감소 로직
        //환불 등..
    }

    //주문 삭제(주문 목록에서)
    public void deleteOrder(Long userId, Long orderId){
        Order order = findByIdOrThrow(orderId);
        checkOwner(order,userId);
        order.softDelete();
    }

    //검즘

    private void checkOwner(Order order, Long userId){
        if(!order.getUser().getId().equals(userId)){
            throw new OrderException(OrderExceptionMessage.ORDER_FORBIDDEN);
        }
    }

    private Order findByIdOrThrow(long id){
        return orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()-> new OrderException(OrderExceptionMessage.ORDER_NOT_FOUND));
    }

    private void existsByUserAndGroupBuy(User user, GroupBuy groupBuy){
        if(orderRepository.existsByUserAndGroupBuy(user,groupBuy)){
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
}
