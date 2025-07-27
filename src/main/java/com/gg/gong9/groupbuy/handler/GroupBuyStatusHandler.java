package com.gg.gong9.groupbuy.handler;

import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.groupbuy.entity.Status;
import com.gg.gong9.notification.sms.service.SmsService;
import com.gg.gong9.notification.sms.util.SmsNotificationType;
import com.gg.gong9.order.entity.Order;
import com.gg.gong9.order.repository.OrderRepository;
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
    private final SmsService smsService;

    public void handleCancelled(GroupBuy groupBuy){
        if (groupBuy.getStatus() != Status.CANCELED) return;

        List<Order> orders = orderRepository.findAllByGroupBuy(groupBuy);

        for (Order order : orders) {
            User orderUser = order.getUser();

            // 활불

            //추후 병렬처리 or 메세지큐로 성능 개선
            try {
                smsService.sendByType(orderUser, SmsNotificationType.GROUP_BUY_CANCELLED);
            } catch (Exception e) {
                log.warn("공구 취소 문자 전송 실패: {}", orderUser.getPhoneNumber(), e);
            }
        }
    }

}
