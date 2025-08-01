package com.gg.gong9.coupon.repository;

import com.gg.gong9.coupon.entity.Coupon;
import com.gg.gong9.coupon.entity.CouponIssue;
import com.gg.gong9.coupon.entity.CouponIssueStatus;
import com.gg.gong9.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponIssueRepository extends JpaRepository<CouponIssue, Long> {
    boolean existsByUserAndCoupon(User user, Coupon coupon);
    List<CouponIssue> findByUser(User user);
    List<CouponIssue> findByStatusAndCoupon_EndAtBefore(CouponIssueStatus status, LocalDateTime now);

}
