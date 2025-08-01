package com.gg.gong9.coupon.entity;

import com.gg.gong9.global.base.BaseEntity;
import com.gg.gong9.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;



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
    private CouponIssueStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;



}
