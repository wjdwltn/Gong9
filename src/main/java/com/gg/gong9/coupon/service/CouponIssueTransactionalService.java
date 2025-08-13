package com.gg.gong9.coupon.service;

import com.gg.gong9.coupon.entity.Coupon;
import com.gg.gong9.coupon.entity.CouponIssue;
import com.gg.gong9.coupon.repository.CouponIssueRepository;
import com.gg.gong9.coupon.repository.CouponRepository;
import com.gg.gong9.global.exception.exceptions.coupon.CouponException;
import com.gg.gong9.global.exception.exceptions.coupon.CouponExceptionMessage;
import com.gg.gong9.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponIssueTransactionalService {

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;

    // 트랜잭션 적용을 위해 클래스 분리
    @Transactional
    public CouponIssue issueCouponWithLock(Long couponId, User user) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponException(CouponExceptionMessage.COUPON_NOT_FOUND));

        if (coupon.getRemainQuantity() <= 0) {
            throw new CouponException(CouponExceptionMessage.COUPON_OUT_OF_STOCK);
        }

        validateNotAlreadyIssued(user, coupon);
        coupon.decreaseRemainQuantity();
        couponRepository.save(coupon);

        CouponIssue issue = CouponIssue.create(user, coupon);
        couponIssueRepository.save(issue);

        return issue;
    }

    private void validateNotAlreadyIssued(User user, Coupon coupon) {
        if (couponIssueRepository.existsByUserAndCoupon(user, coupon)) {
            throw new CouponException(CouponExceptionMessage.COUPON_ALREADY_ISSUED);
        }
    }
}
