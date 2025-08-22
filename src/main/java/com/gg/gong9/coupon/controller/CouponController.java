package com.gg.gong9.coupon.controller;

import com.gg.gong9.coupon.controller.dto.*;
import com.gg.gong9.coupon.entity.Coupon;
import com.gg.gong9.coupon.service.CouponService;
import com.gg.gong9.global.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller/coupons")
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<CouponCreateResponseDto> createCoupon(
            @Valid @RequestBody CouponCreateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        Coupon saved = couponService.createCoupon(dto, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CouponCreateResponseDto(saved.getId(), "쿠폰 생성이 완료되었습니다."));
    }

    @GetMapping
    public ResponseEntity<List<CouponListResponseDto>> getCoupons(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        return ResponseEntity.ok(couponService.getCoupons(userDetails.getUser()));
    }

    @GetMapping("/available")
    public ResponseEntity<List<CouponListResponseDto>> getAvailableCoupons(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        return ResponseEntity.ok(couponService.getAvailableCoupons(userDetails.getUser()));
    }

    @PutMapping("/{couponId}")
    public ResponseEntity<CouponResponse> updateCoupon(
            @PathVariable Long couponId,
            @Valid @RequestBody CouponUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        couponService.updateCoupon(couponId, dto, userDetails.getUser());
        return ResponseEntity.ok(new CouponResponse("쿠폰 수정이 완료되었습니다."));
    }

    @DeleteMapping("/{couponId}")
    public ResponseEntity<CouponResponse> deleteCoupon(
            @PathVariable("couponId") Long couponId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        couponService.deleteCoupon(couponId, userDetails.getUser());
        return ResponseEntity.ok(new CouponResponse("쿠폰 삭제가 완료되었습니다."));
    }
}
