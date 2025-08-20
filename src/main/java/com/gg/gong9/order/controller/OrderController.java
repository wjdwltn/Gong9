package com.gg.gong9.order.controller;

import com.gg.gong9.global.security.jwt.CustomUserDetails;
import com.gg.gong9.order.controller.dto.OrderDetailResponse;
import com.gg.gong9.order.controller.dto.OrderListResponse;
import com.gg.gong9.order.controller.dto.OrderRequest;
import com.gg.gong9.order.controller.dto.OrderResponse;
import com.gg.gong9.order.entity.Order;
import com.gg.gong9.order.service.OrderService;
import com.gg.gong9.order.service.concurrency.redis_lau.OrderLauScriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderLauScriptService orderLauScriptService;

    //주문 생성
    @PostMapping("/kafka")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request,
                                                @AuthenticationPrincipal CustomUserDetails userPrincipal){
        Long userId = userPrincipal.getUser().getId();
        orderLauScriptService.tryCreateOrderWithRedisWithKafka(userId, request);
        return ResponseEntity.ok("주문이 생성되었습니다.");
    }

    //주문 생성
//    @PostMapping
//    public ResponseEntity<OrderResponse> create(@RequestBody OrderRequest request,
//                                                @AuthenticationPrincipal CustomUserDetails userPrincipal){
//        Long userId = userPrincipal.getUser().getId();
//        Order order = orderLauScriptService.tryCreateOrderWithRedis(userId, request);
//        return ResponseEntity.ok(new OrderResponse(order.getId(),"주문이 생성되었습니다."));
//    }

    //주문 목록 조회(내가 주문한 목록)
    @GetMapping("/me")
    public ResponseEntity<List<OrderListResponse>> getOrderList(@AuthenticationPrincipal CustomUserDetails userPrincipal){
        Long userId = userPrincipal.getUser().getId();
        return ResponseEntity.ok(orderService.getOrderList(userId));
    }

    //주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable Long orderId,
                                                              @AuthenticationPrincipal CustomUserDetails userPrincipal){
        Long userId = userPrincipal.getUser().getId();
        return ResponseEntity.ok(orderService.getOrderDetail(userId, orderId));
    }

    //주문 취소
    @PostMapping("/{orderId}")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId,
                                                     @AuthenticationPrincipal CustomUserDetails userPrincipal){
        Long userId = userPrincipal.getUser().getId();
        orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok(new OrderResponse(orderId ,"주문이 취소되었습니다."));
    }

    //주문 삭제
    @DeleteMapping("/{orderId}")
    public ResponseEntity<OrderResponse> deleteOrder(@PathVariable Long orderId,
                                                     @AuthenticationPrincipal CustomUserDetails userPrincipal){
        Long userId = userPrincipal.getUser().getId();
        orderService.deleteOrder(userId, orderId);
        return ResponseEntity.ok(new OrderResponse(orderId ,"주문이 삭제되었습니다."));
    }
}
