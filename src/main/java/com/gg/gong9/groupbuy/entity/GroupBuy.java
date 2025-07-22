package com.gg.gong9.groupbuy.entity;

import com.gg.gong9.global.base.BaseEntity;
import com.gg.gong9.product.entity.Product;
import com.gg.gong9.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@Table(name = "group_buy")
@NoArgsConstructor
public class GroupBuy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_buy_id", nullable = false)
    private Long id;

    private int totalQuantity;
    private int limitQuantity;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


}
