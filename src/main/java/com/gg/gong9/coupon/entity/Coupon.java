package com.gg.gong9.coupon.entity;

import com.gg.gong9.global.base.BaseEntity;
import com.gg.gong9.groupbuy.entity.GroupBuy;
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

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_buy_id")
    private GroupBuy groupBuy;


    public static Coupon create(String name, int quantity, int discount, int min_order_price, CouponStatus status,
                                LocalDateTime startAt, LocalDateTime endAt, User user, GroupBuy groupBuy) {
        Coupon coupon = new Coupon();
        coupon.name = name;
        coupon.quantity = quantity;
        coupon.discount = discount;
        coupon.min_order_price = min_order_price;
        coupon.status = status;
        coupon.startAt = startAt;
        coupon.endAt = endAt;
        coupon.user = user;
        coupon.groupBuy = groupBuy;
        return coupon;
    }

    public void update(String name, int quantity, int min_order_price,int discount, LocalDateTime startAt, LocalDateTime endAt) {
        this.name = name;
        this.quantity = quantity;
        this.min_order_price = min_order_price;
        this.discount = discount;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public boolean editable() {
        return LocalDateTime.now().isBefore(this.startAt);
    }

    public void markAsExpired() {
        this.status = CouponStatus.EXPIRED;
    }

}
