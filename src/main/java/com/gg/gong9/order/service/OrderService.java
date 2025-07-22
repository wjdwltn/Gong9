package com.gg.gong9.order.service;

import com.gg.gong9.global.exception.exceptions.order.OrderException;
import com.gg.gong9.global.exception.exceptions.order.OrderExceptionMessage;
import com.gg.gong9.global.exception.exceptions.user.UserException;
import com.gg.gong9.global.exception.exceptions.user.UserExceptionMessage;
import com.gg.gong9.order.controller.dto.OrderDetailResponse;
import com.gg.gong9.order.controller.dto.OrderListResponse;
import com.gg.gong9.order.controller.dto.OrderRequest;
import com.gg.gong9.order.controller.dto.OrderResponse;
import com.gg.gong9.order.entity.Order;
import com.gg.gong9.order.entity.OrderStatus;
import com.gg.gong9.order.repository.OrderRepository;
import com.gg.gong9.product.entity.Product;
import com.gg.gong9.product.repository.ProductRepository;
import com.gg.gong9.product.service.ProductService;
import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.repository.UserRepository;
import com.gg.gong9.user.service.UserService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    //private final GroupBuy groupBuy;

    //주문 생성
    public Order createOrder(Long userId, OrderRequest request){

        User user = userService.findByIdOrThrow(userId);

        //GroupBuy groupBuy = groupBuyService.getGroupBuyOrThrow(request.groupBuyId());

        Order order = Order.builder()
                .quantity(request.quantity())
                .status(OrderStatus.PAYMENT_COMPLETED)
                .user(user)
                //.groupBuy(groupBuy)
                .build();

        return orderRepository.save(order);
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
        checkOwner(order,userId);
        order.softDelete();
    }


    //주문 삭제
    public void deleteOrder(Long userId, Long orderId){
        Order order = findByIdOrThrow(orderId);
        checkOwner(order,userId);
        orderRepository.deleteById(orderId);
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
}
