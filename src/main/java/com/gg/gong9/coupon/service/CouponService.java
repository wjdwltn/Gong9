package com.gg.gong9.coupon.service;

import com.gg.gong9.coupon.controller.dto.CouponCreateRequestDto;
import com.gg.gong9.coupon.controller.dto.CouponListResponseDto;
import com.gg.gong9.coupon.controller.dto.CouponUpdateRequestDto;
import com.gg.gong9.coupon.entity.Coupon;
import com.gg.gong9.coupon.entity.CouponIssue;
import com.gg.gong9.coupon.entity.CouponIssueStatus;
import com.gg.gong9.coupon.entity.CouponStatus;
import com.gg.gong9.coupon.repository.CouponIssueRepository;
import com.gg.gong9.coupon.repository.CouponRepository;
import com.gg.gong9.global.exception.exceptions.coupon.CouponException;
import com.gg.gong9.global.exception.exceptions.coupon.CouponExceptionMessage;
import com.gg.gong9.global.exception.exceptions.user.UserException;
import com.gg.gong9.global.exception.exceptions.user.UserExceptionMessage;

import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.entity.UserRole;
import com.gg.gong9.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final CouponRedisStockService couponRedisService;


    // 판매자 쿠폰 생성
    @Transactional
    public Coupon createCoupon(CouponCreateRequestDto dto, User user) {

        validateAdmin(user);

        validateUserExists(user.getId());

        validateStartBeforeEnd(dto.startAt(), dto.endAt());

        Coupon coupon = Coupon.create(
                dto.name(),
                dto.quantity(),
                dto.discount(),
                dto.minOrderPrice(),
                CouponStatus.ACTIVE,
                dto.startAt(),
                dto.endAt(),
                user
        );

        couponRedisService.initializeStockAndUserOrders(coupon.getId(),dto.quantity());

        return couponRepository.save(coupon);

    }

    // 판매자 쿠폰 목록 조회
    @Transactional(readOnly = true)
    public List<CouponListResponseDto> getCoupons(User user) {
        return couponRepository.findByUserId(user.getId()).stream()
                .map(coupon -> {
                    int currentStock = couponRedisService.getCurrentStock(coupon.getId()); //현재 재고
                    int usedQuantity = coupon.getQuantity() - currentStock; // 총 구매 수량
                    return new CouponListResponseDto(coupon, currentStock, usedQuantity,true);
                })
                .toList();
    }

    //구매자 쿠폰 목록 조회
    @Transactional(readOnly = true)
    public List<CouponListResponseDto> getAvailableCoupons(User user) {

        Set<Long> issuedCouponIds = couponIssueRepository.findByUserAndStatus(user, CouponIssueStatus.UNUSED).stream()
                .map(issue -> issue.getCoupon().getId())
                .collect(Collectors.toSet());

        List<Coupon> availableCoupons = couponRepository.findAvailableCoupons(LocalDateTime.now());

        return availableCoupons.stream()
                .map(coupon -> {
                    int currentStock = couponRedisService.getCurrentStock(coupon.getId());
                    int usedQuantity = coupon.getQuantity() - currentStock;
                    boolean alreadyIssued = issuedCouponIds.contains(coupon.getId()); // 발급 여부
                    return new CouponListResponseDto(coupon, currentStock, usedQuantity,alreadyIssued);
                })
                .toList();
    }

    // 쿠폰 수정
    @Transactional
    public void updateCoupon(Long couponId, CouponUpdateRequestDto dto, User user) {
        Coupon coupon = getCoupon(couponId);

        validateCouponOwner(coupon, user);

        validateStartBeforeEnd(dto.startAt(), dto.endAt());

        if(!coupon.editable()){
            throw new CouponException(CouponExceptionMessage.COUPON_ALREADY_STARTED);
        }

        coupon.update(
                dto.name(),
                dto.quantity(),
                dto.minOrderPrice(),
                dto.discount(),
                dto.startAt(),
                dto.endAt()
        );
    }

    // 쿠폰 삭제
    @Transactional
    public void deleteCoupon(Long couponId, User user) {
        Coupon coupon = getCoupon(couponId);
        validateCouponOwner(coupon, user);

        if(!coupon.editable()){
            throw new CouponException(CouponExceptionMessage.COUPON_DELETE_FORBIDDEN);
        }
        couponRepository.delete(coupon);

        couponRedisService.deleteGroupBuyData(couponId);
    }


    private Coupon getCoupon(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponException(CouponExceptionMessage.COUPON_NOT_FOUND));
    }

    private void validateAdmin(User user) {
        if (!user.getUserRole().equals(UserRole.ADMIN)) {
            throw new UserException(UserExceptionMessage.ADMIN_ACCESS_ONLY);
        }
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserException(UserExceptionMessage.USER_NOT_FOUND);
        }
    }

    private void validateCouponOwner(Coupon coupon, User user) {
        if (!coupon.getUser().getId().equals(user.getId())) {
            throw new CouponException(CouponExceptionMessage.COUPON_NO_AUTHORITY);
        }
    }

    private void validateStartBeforeEnd(LocalDateTime startAt, LocalDateTime endAt) {
        if (endAt.isBefore(startAt)) {
            throw new CouponException(CouponExceptionMessage.INVALID_END_TIME);
        }
    }
}
