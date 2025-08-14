package com.gg.gong9.order.handler;

import com.gg.gong9.notification.sms.controller.SmsNotificationType;
import com.gg.gong9.notification.sms.service.SmsNotificationService;
import com.gg.gong9.order.controller.dto.OrderCancelledEvent;
import com.gg.gong9.order.service.OrderRedisStockService;
import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderCancelledEventHandler {

    private final UserService userService;
    private final SmsNotificationService smsNotificationService;
    private final OrderRedisStockService orderRedisStockService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCancelled(OrderCancelledEvent event) {
        User user = userService.findByIdOrThrow(event.userId());

        //redis 재고 감소
        orderRedisStockService.increaseStockAndRemoveUserOrder(event.groupBuyId(), event.userId(), event.quantity());

        // Kafka로 비동기 문자 발송
        smsNotificationService.sendSms(user, SmsNotificationType.ORDER_CANCELLED);
    }
}
