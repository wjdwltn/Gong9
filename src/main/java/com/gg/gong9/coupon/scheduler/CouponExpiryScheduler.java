package com.gg.gong9.coupon.scheduler;

import com.gg.gong9.coupon.entity.CouponIssue;
import com.gg.gong9.coupon.entity.CouponIssueStatus;
import com.gg.gong9.coupon.repository.CouponIssueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponExpiryScheduler {

    private final CouponIssueRepository couponIssueRepository;

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void expireCoupons() {
        List<CouponIssue> couponIssues = couponIssueRepository
                .findByStatusAndCoupon_EndAtBefore(CouponIssueStatus.UNUSED, LocalDateTime.now());

        for(CouponIssue couponIssue : couponIssues) {
            couponIssue.markAsExpired();
        }
    }
}
