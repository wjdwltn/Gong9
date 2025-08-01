package com.gg.gong9.coupon.service;

import com.gg.gong9.coupon.controller.dto.CouponCreateRequestDto;
import com.gg.gong9.coupon.controller.dto.CouponListResponseDto;
import com.gg.gong9.coupon.controller.dto.CouponUpdateRequestDto;
import com.gg.gong9.coupon.entity.Coupon;
import com.gg.gong9.coupon.repository.CouponRepository;
import com.gg.gong9.global.exception.exceptions.coupon.CouponException;
import com.gg.gong9.global.exception.exceptions.coupon.CouponExceptionMessage;
import com.gg.gong9.global.exception.exceptions.user.UserException;
import com.gg.gong9.global.exception.exceptions.user.UserExceptionMessage;

import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.entity.UserRole;
import com.gg.gong9.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;


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
                dto.startAt(),
                dto.endAt(),
                user
        );
        return couponRepository.save(coupon);
    }

    // 판매자 쿠폰 목록 조회
    @Transactional (Transactional.TxType.SUPPORTS)
    public List<CouponListResponseDto> getCoupons(User user) {
        return couponRepository.findByUserId(user.getId()).stream()
                .map(CouponListResponseDto::from)
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
