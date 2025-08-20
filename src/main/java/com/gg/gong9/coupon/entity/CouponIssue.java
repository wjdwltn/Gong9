package com.gg.gong9.coupon.entity;

import com.gg.gong9.global.base.BaseEntity;
import com.gg.gong9.global.exception.exceptions.coupon.CouponException;
import com.gg.gong9.global.exception.exceptions.coupon.CouponExceptionMessage;
import com.gg.gong9.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@Table(name = "coupon_issue")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponIssue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_issue_id",nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @UniqueConstraint(columnNames = {"user_id", "coupon_id"})
    private CouponIssueStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    public static CouponIssue create(User user, Coupon coupon) {
        CouponIssue issue = new CouponIssue();
        issue.user = user;
        issue.coupon = coupon;
        issue.status = CouponIssueStatus.UNUSED;
        return issue;
    }

    public void markAsUsed() {
        this.status = CouponIssueStatus.USED;
    }

    public void markAsExpired() {
        this.status = CouponIssueStatus.EXPIRED;
    }

    public void validateUsable() {
        if (this.status != CouponIssueStatus.UNUSED) {
            throw new CouponException(CouponExceptionMessage.COUPON_INVALID_STATUS);
        }

        if (this.coupon.getEndAt().isBefore(LocalDateTime.now())) {
            this.markAsExpired();
            throw new CouponException(CouponExceptionMessage.COUPON_EXPIRED);
        }
    }

}
