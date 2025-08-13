package com.gg.gong9.coupon.kafka;

import com.gg.gong9.coupon.controller.dto.CouponIssuedEvent;
import com.gg.gong9.coupon.entity.Coupon;
import com.gg.gong9.coupon.entity.CouponIssue;
import com.gg.gong9.coupon.repository.CouponIssueRepository;
import com.gg.gong9.coupon.repository.CouponRepository;
import com.gg.gong9.global.exception.exceptions.coupon.CouponException;
import com.gg.gong9.global.exception.exceptions.coupon.CouponExceptionMessage;
import com.gg.gong9.global.exception.exceptions.user.UserException;
import com.gg.gong9.global.exception.exceptions.user.UserExceptionMessage;
import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponConsumer {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final CouponIssueRepository couponIssueRepository;

    @KafkaListener(topics = "coupon-issued", groupId = "coupon-issue-group")
    @Transactional
    public void consumeCouponIssue(CouponIssuedEvent event) {
        log.info(" 카프카 컨슈며 이벤트 수신 userId={}, couponId={}", event.userId(), event.couponId());

        User user = userRepository.findById(event.userId())
                .orElseThrow(() -> new UserException(UserExceptionMessage
                        .USER_NOT_FOUND));

        Coupon coupon = couponRepository.findById(event.couponId())
                .orElseThrow(() -> new CouponException(CouponExceptionMessage.COUPON_NOT_FOUND));

        if (couponIssueRepository.existsByUserAndCoupon(user, coupon)) {
            log.info("카프카 컨슈머 이미 쿠폰 발급됨 userId={}, couponId={}", user.getId(), coupon.getId());
            return;
        }

        CouponIssue issue = CouponIssue.create(user, coupon);
        couponIssueRepository.save(issue);
    }
}

