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
import com.gg.gong9.global.manager.RedissonLockManager;
import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssueService {

    private final CouponIssueRepository couponIssueRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final RedissonLockManager redissonLockManager;
    private final CouponIssueTransactionalService couponIssueTransactionalService;


    // Redisson 분산락 적용
    public CouponIssue issueCoupon(Long couponId, User user) {
        String lockKey = "couponLock:" + couponId;
        RLock lock = redissonLockManager.lock(lockKey, 10, 3);

        if (lock == null) throw new CouponException(CouponExceptionMessage.COUPON_LOCK_TIMEOUT);

        try {
            return couponIssueTransactionalService.issueCouponWithLock(couponId, user);
        } finally {
            redissonLockManager.unlock(lock);
        }
    }


//    //     쿠폰 발급 (동시성 제어 => 비관적 락)
//    @Transactional
//    public CouponIssue issueCoupon(Long couponId, User user) {
//        validateUser(user.getId());
//
//        try {
//            Coupon coupon = couponRepository.findByWithLock(couponId)
//                    .orElseThrow(() -> new CouponException(CouponExceptionMessage.COUPON_NOT_FOUND));
//
//            if (couponIssueRepository.existsByUserAndCoupon(user, coupon)) {
//                throw new CouponException(CouponExceptionMessage.COUPON_ALREADY_ISSUED);
//            }
//
//            if (coupon.getRemainQuantity() <= 0) {
//                throw new CouponException(CouponExceptionMessage.COUPON_SOLD_OUT);
//            }
//
//            coupon.decreaseRemainQuantity();
//
//            CouponIssue issue = CouponIssue.create(user, coupon);
//            return couponIssueRepository.save(issue);
//
//        } catch (jakarta.persistence.PessimisticLockException e) {
//            throw new CouponException(CouponExceptionMessage.COUPON_LOCK_TIMEOUT);
//        }
//    }

//    //   쿠폰 발급 (동시성 제어 x )
//    @Transactional
//    public CouponIssue issueCoupon(Long couponId, User user) {
//        validateUser(user.getId());
//
//        Coupon coupon = couponRepository.findById(couponId)
//                .orElseThrow(()-> new CouponException(CouponExceptionMessage.COUPON_NOT_FOUND));
//
//        // 동시성 제어 도입 예정
//        if(couponIssueRepository.existsByUserAndCoupon(user, coupon)) {
//            throw new CouponException(CouponExceptionMessage.COUPON_ALREADY_ISSUED);
//        }
//
//        coupon.decreaseRemainQuantity();
//
//        CouponIssue issue = CouponIssue.create(user, coupon);
//        return couponIssueRepository.save(issue);
//    }

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

    private Coupon getCouponOrThrow(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponException(CouponExceptionMessage.COUPON_NOT_FOUND));
    }

    private void validateNotAlreadyIssued(User user, Coupon coupon) {
        if (couponIssueRepository.existsByUserAndCoupon(user, coupon)) {
            throw new CouponException(CouponExceptionMessage.COUPON_ALREADY_ISSUED);
        }
    }
}
