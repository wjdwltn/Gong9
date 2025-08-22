package com.gg.gong9.coupon.scheduler;

import com.gg.gong9.coupon.entity.Coupon;
import com.gg.gong9.coupon.entity.CouponIssue;
import com.gg.gong9.coupon.entity.CouponIssueStatus;
import com.gg.gong9.coupon.entity.CouponStatus;
import com.gg.gong9.coupon.repository.CouponIssueRepository;
import com.gg.gong9.coupon.service.RedisCouponService;
import jakarta.transaction.Transactional;
import com.gg.gong9.coupon.repository.CouponRepository;
import com.gg.gong9.coupon.service.CouponIssueService;
import com.gg.gong9.coupon.service.CouponRedisStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponExpiryScheduler {

    private final CouponIssueRepository couponIssueRepository;
    private final RedisCouponService redisCouponService;
    private final CouponIssueService couponIssueService;
    private final CouponRepository couponRepository;
    private final CouponRedisStockService couponRedisService;

//    @Transactional
//    @Scheduled(cron = "0 0 * * * *")
//    public void expireCoupons() {
//        List<CouponIssue> couponIssues = couponIssueRepository
//                .findByStatusAndCoupon_EndAtBefore(CouponIssueStatus.UNUSED, LocalDateTime.now());
//
//        for(CouponIssue couponIssue : couponIssues) {
//            couponIssue.markAsExpired();
//        }
//    }

    @Scheduled(cron = "0 0 * * * *")
    public void expireCoupons() {
        List<Coupon> expiredCoupons = couponRepository.findByStatusAndEndAtBefore(CouponStatus.ACTIVE,LocalDateTime.now());

        for(Coupon coupon : expiredCoupons){
            Long couponId = coupon.getId();

            try{
                int updateCount = couponIssueService.expireCouponIssues(coupon);

                if (updateCount > 0) {
                    log.info("[쿠폰 만료 처리] couponId={}, 만료된 발급 수={}", couponId, updateCount);
                    couponRedisService.deleteGroupBuyData(couponId);
                }

        for(CouponIssue couponIssue : couponIssues) {
            couponIssue.markAsExpired();

            redisCouponService.deleteCouponKeys(couponIssue.getCoupon().getId(), List.of(couponIssue.getUser().getId()));
            }catch (Exception e) {
                log.error("[쿠폰 만료 실패] couponId={}", couponId, e);
            }
        }
    }

    //추후 테스트 -> 쿠폰 id 리스트 모아서 -> db에 한 번의 벌크 쿼리로 쿠폰 상태 변경 -> 트랜잭션 1회로 최적화
    @Scheduled(cron = "0 0 * * * *") // 매시간 실행
    public void expireCoupons_update() {
        LocalDateTime now = LocalDateTime.now();

        List<Coupon> expiredCoupons = couponRepository.findByStatusAndEndAtBefore(CouponStatus.ACTIVE,LocalDateTime.now());

        if (expiredCoupons.isEmpty()) {
            log.info("[쿠폰 만료 처리] 마감된 쿠폰 없음");
            return;
        }

        List<Long> couponIds = expiredCoupons.stream()
                .map(Coupon::getId)
                .toList();

        try {
            int updatedCount = couponIssueService.expireCouponIssuesBulk(couponIds);
            log.info("[쿠폰 만료 처리] 쿠폰 발급 건수={}건, 실제 만료된 발급 수={}", couponIds.size(), updatedCount);
        } catch (Exception e) {
            log.error("[쿠폰 만료 실패] DB 벌크 업데이트 오류", e);
            return;
        }

        couponIds.forEach(couponRedisService::deleteGroupBuyData);
    }

}
