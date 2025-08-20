package com.gg.gong9.notification.sms.controller;

public enum SmsNotificationType {
    GROUP_BUY_SUCCESS,     // 공고 모집 완료
    GROUP_BUY_CANCELLED,   // 공고 진행 취소
    ORDER_SUCCESS,         //주문 완료
    ORDER_CANCELLED,       //주문 취소
    DELIVERY_STARTED,      // 배송 시작
    DELIVERY_COMPLETED,    // 배송 완료
    REFUND_COMPLETED       // 환불 처리
}
