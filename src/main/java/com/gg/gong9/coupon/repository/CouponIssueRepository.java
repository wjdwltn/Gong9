package com.gg.gong9.coupon.repository;

import com.gg.gong9.coupon.entity.Coupon;
import com.gg.gong9.coupon.entity.CouponIssue;
import com.gg.gong9.coupon.entity.CouponIssueStatus;
import com.gg.gong9.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponIssueRepository extends JpaRepository<CouponIssue, Long> {
    boolean existsByUserAndCoupon(User user, Coupon coupon);
    List<CouponIssue> findByUser(User user);
    List<CouponIssue> findByUserAndStatusNot(User user, CouponIssueStatus status);

    @Modifying
    @Query("UPDATE CouponIssue ci SET ci.status = :newStatus " +
            "WHERE ci.coupon.id = :couponId AND ci.status = 'UNUSED'")
    int updateStatusToExpired(@Param("couponId") Long couponId,
                              @Param("newStatus") CouponIssueStatus newStatus);

    @Modifying
    @Query("UPDATE CouponIssue ci SET ci.status = :newStatus " +
            "WHERE ci.coupon.id IN :couponIds AND ci.status = 'UNUSED'")
    int updateStatusToExpiredBulk(@Param("couponIds") List<Long> couponIds,
                                  @Param("newStatus") CouponIssueStatus newStatus);

}
