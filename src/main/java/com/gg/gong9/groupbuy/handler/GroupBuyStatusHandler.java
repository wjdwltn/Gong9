package com.gg.gong9.groupbuy.handler;

import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.notification.sms.controller.SmsNotificationType;
import com.gg.gong9.notification.sms.controller.dto.SmsMessage;
import com.gg.gong9.notification.sms.kafka.SmsKafkaProducer;
import com.gg.gong9.notification.sms.service.SmsNotificationService;
import com.gg.gong9.notification.sms.service.SmsService;
import com.gg.gong9.order.entity.Order;
import com.gg.gong9.order.entity.OrderStatus;
import com.gg.gong9.order.repository.OrderRepository;
import com.gg.gong9.order.service.OrderService;
import com.gg.gong9.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupBuyStatusHandler {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final SmsNotificationService smsNotificationService;

    public void handleCancelled(GroupBuy groupBuy){
        if (groupBuy.getStatus() != BuyStatus.CANCELED) return;

        List<User> allUsers = orderService.findAllUsersByGroupBuy(groupBuy.getId());

        orderRepository.updateStatusByGroupBuyId(OrderStatus.CANCELLED, groupBuy.getId());

        //추후 결제 환불

        //주문 취소 문자 카프카 배치 처리
        smsNotificationService.sendBulkSms(allUsers,SmsNotificationType.GROUP_BUY_CANCELLED);

    }

}
