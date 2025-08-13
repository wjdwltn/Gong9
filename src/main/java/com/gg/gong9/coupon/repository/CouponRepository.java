package com.gg.gong9.coupon.repository;

import com.gg.gong9.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByUserId(Long userId);

    List<Coupon> findByEndAtBefore(LocalDateTime dateTime);
}
