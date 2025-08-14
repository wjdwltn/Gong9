package com.gg.gong9.groupbuy.handler;

import com.gg.gong9.groupbuy.controller.dto.GroupBuyCancelledEvent;
import com.gg.gong9.notification.sms.controller.SmsNotificationType;
import com.gg.gong9.notification.sms.service.SmsNotificationService;
import com.gg.gong9.order.entity.OrderStatus;
import com.gg.gong9.order.repository.OrderRepository;
import com.gg.gong9.order.service.OrderService;
import com.gg.gong9.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupBuyCancelledEventHandler {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final SmsNotificationService smsNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(GroupBuyCancelledEvent event) {
        Long groupBuyId = event.groupBuyId();

        List<User> allUsers = orderService.findAllUsersByGroupBuy(groupBuyId);

        //주문 상태 변경(벌크)
        orderRepository.updateStatusByGroupBuyId(OrderStatus.CANCELLED, groupBuyId, OrderStatus.PAYMENT_COMPLETED);

        //주문 취소 문자 카프카 배치 처리
        smsNotificationService.sendBulkSms(allUsers, SmsNotificationType.GROUP_BUY_CANCELLED);

        //추후 환불 처리
    }
}
