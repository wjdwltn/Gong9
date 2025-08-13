package com.gg.gong9.coupon.repository;

import com.gg.gong9.coupon.entity.Coupon;
import com.gg.gong9.coupon.entity.CouponIssue;
import com.gg.gong9.coupon.entity.CouponIssueStatus;
import com.gg.gong9.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponIssueRepository extends JpaRepository<CouponIssue, Long> {
    boolean existsByUserAndCoupon(User user, Coupon coupon);
    List<CouponIssue> findByUser(User user);
    List<CouponIssue> findByStatusAndCoupon_EndAtBefore(CouponIssueStatus status, LocalDateTime now);

    @Modifying
    @Query("UPDATE CouponIssue ci SET ci.status = :expired " +
            "WHERE ci.coupon.id = :couponId AND ci.status = :unused")
    int updateStatusToExpired(@Param("couponId") Long couponId,
                              @Param("unused") CouponIssueStatus unused,
                              @Param("expired") CouponIssueStatus expired);

    @Modifying
    @Query("UPDATE CouponIssue ci SET ci.status = :newStatus " +
            "WHERE ci.coupon.id IN :couponIds AND ci.status = :unused")
    int updateStatusToExpiredBulk(@Param("couponIds") List<Long> couponIds,
                                  @Param("unused") CouponIssueStatus unused,
                                  @Param("expired") CouponIssueStatus expired);

}
