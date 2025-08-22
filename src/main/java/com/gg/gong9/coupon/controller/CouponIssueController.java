package com.gg.gong9.coupon.controller;

import com.gg.gong9.coupon.controller.dto.CouponIssueCreateResponseDto;
import com.gg.gong9.coupon.controller.dto.CouponIssueListResponseDto;
import com.gg.gong9.coupon.controller.dto.CouponIssueResponse;
import com.gg.gong9.coupon.entity.CouponIssue;
import com.gg.gong9.coupon.service.CouponIssueService;
import com.gg.gong9.global.security.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponIssueController {

    private final CouponIssueService couponIssueService;

    // 쿠폰 발급
    @PostMapping("/{couponId}/issue")
    public ResponseEntity<String> issueCoupon(
            @PathVariable("couponId") Long couponId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        couponIssueService.issueCoupon(couponId, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("쿠폰 발급이 완료되었습니다.");
    }

    // 그 외 (kafka 처리 전)
//    @PostMapping("/{couponId}/issue")
//    public ResponseEntity<CouponIssueCreateResponseDto> issueCoupon(
//            @PathVariable("couponId") Long couponId,
//            @AuthenticationPrincipal CustomUserDetails userDetails
//    ) {
//        CouponIssue saved = couponIssueService.issueCoupon(couponId, userDetails.getUser());
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(new CouponIssueCreateResponseDto(saved.getId(), "쿠폰 발급이 완료되었습니다."));
//    }

    // 자신이 발급받은 쿠폰 목록 조회
    @GetMapping("/issued")
    public ResponseEntity<List<CouponIssueListResponseDto>> getCouponIssues(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        List<CouponIssueListResponseDto> response = couponIssueService.getIssuedCoupons(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    // 쿠폰 사용 완료
    @PutMapping("/{couponIssueId}/used")
    public ResponseEntity<CouponIssueResponse> usedCoupon(
            @PathVariable Long couponIssueId,
            @RequestParam Long groupBuyId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        couponIssueService.useCoupon(couponIssueId, userDetails.getUser(), groupBuyId);
        return ResponseEntity.ok(new CouponIssueResponse("쿠폰 사용이 정상적으로 처리되었습니다."));
    }

}
