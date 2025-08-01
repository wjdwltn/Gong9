package com.gg.gong9.coupon.entity;

import com.gg.gong9.global.base.BaseEntity;
import com.gg.gong9.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "coupon")
@NoArgsConstructor
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id",nullable = false)
    private Long id;

    private String name;

    private int quantity;

    private int remainQuantity;

    private int discount;

    private int min_order_price;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;




}
