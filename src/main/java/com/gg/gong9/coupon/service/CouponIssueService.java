package com.gg.gong9.coupon.service;

import com.gg.gong9.coupon.controller.dto.CouponIssueListResponseDto;
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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponIssueService {

    private final CouponIssueRepository couponIssueRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    // 쿠폰 발급
    @Transactional
    public CouponIssue issueCoupon(Long couponId, User user) {
        validateUser(user.getId());

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(()-> new CouponException(CouponExceptionMessage.COUPON_NOT_FOUND));

        // 동시성 제어 도입 예정
        if(couponIssueRepository.existsByUserAndCoupon(user, coupon)) {
            throw new CouponException(CouponExceptionMessage.COUPON_ALREADY_ISSUED);
        }
        CouponIssue issue = CouponIssue.create(user, coupon);
        return couponIssueRepository.save(issue);
    }

    // 발급 받은 쿠폰 목록 조회
    public List<CouponIssueListResponseDto> getIssuedCoupons(User user) {
        validateUser(user.getId());

        List<CouponIssue> issuedCoupons = couponIssueRepository.findByUser(user);

        return issuedCoupons.stream()
                .map(CouponIssueListResponseDto::from)
                .toList();
    }

    // 쿠폰 사용 완료처리 (결제 후 완료로..) UNUSED → USED
    @Transactional
    public void useCoupon(Long couponId, User user) {

        CouponIssue couponIssue = getCouponIssue(couponId);

        validateCouponOwner(couponIssue, user.getId());

        couponIssue.validateUsable(); //(스케줄러 지연 보완)
        couponIssue.markAsUsed();

    }

    private CouponIssue getCouponIssue(Long couponIssueId) {
        return couponIssueRepository.findById(couponIssueId)
                .orElseThrow(() -> new CouponException(CouponExceptionMessage.COUPON_NOT_FOUND));
    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserException(UserExceptionMessage.USER_NOT_FOUND);
        }
    }

    private void validateCouponOwner(CouponIssue couponIssue, Long userId) {
        if (!couponIssue.getUser().getId().equals(userId)) {
            throw new CouponException(CouponExceptionMessage.COUPON_NO_AUTHORITY);
        }
    }
}
