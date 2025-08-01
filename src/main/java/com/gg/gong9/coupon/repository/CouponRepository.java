package com.gg.gong9.coupon.repository;

import com.gg.gong9.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;



public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
