package com.gg.gong9.coupon.repository;

import com.gg.gong9.coupon.entity.Coupon;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import com.gg.gong9.coupon.entity.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")}) // (타임아웃은 3초)
    @Query("select c from Coupon c where c.id = :couponId")
    Optional<Coupon> findByWithLock(@Param("couponId") Long couponId);

    List<Coupon> findByStatusAndEndAtBefore(CouponStatus status, LocalDateTime now);

    @Query("SELECT c FROM Coupon c WHERE c.endAt > :now AND c.status = 'ACTIVE'")
    List<Coupon> findAvailableCoupons(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Coupon c SET c.status = :newStatus WHERE c.id IN :couponIds AND c.status = 'ACTIVE'")
    int updateStatusToExpiredBulk(@Param("couponIds") List<Long> couponIds,
                                  @Param("newStatus") CouponStatus newStatus);
}
