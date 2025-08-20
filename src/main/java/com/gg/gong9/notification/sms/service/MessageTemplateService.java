package com.gg.gong9.notification.sms.service;

import com.gg.gong9.notification.sms.controller.SmsNotificationType;
import org.springframework.stereotype.Component;

@Component
public class MessageTemplateService {

    private static final String PREFIX = "[공구리]\n";

    public String generateMessageForType(SmsNotificationType type, String userName) {
        String template = switch (type) {
            case GROUP_BUY_SUCCESS -> "공동구매 모집이 완료되었습니다!";
            case GROUP_BUY_CANCELLED -> "공동구매 진행이 취소되었습니다. 환불 처리는 최대 3일 이내에 완료될 예정입니다.";
            case ORDER_SUCCESS -> "%s님, 주문이 정상적으로 완료되었습니다!";
            case ORDER_CANCELLED -> "주문이 정상적으로 취소되었습니다. 환불 처리는 최대 3일 이내에 완료될 예정입니다.";
            case DELIVERY_STARTED -> "상품이 발송되었습니다!";
            case DELIVERY_COMPLETED -> "배송이 완료되었습니다. 감사합니다!";
            case REFUND_COMPLETED -> "환불이 정상적으로 처리되었습니다.";
        };

        return PREFIX + (template.contains("%s") ? String.format(template, userName) : template);
    }
}
