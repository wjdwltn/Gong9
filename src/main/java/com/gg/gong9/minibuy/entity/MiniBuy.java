package com.gg.gong9.minibuy.entity;

import com.gg.gong9.global.base.BaseEntity;
import com.gg.gong9.global.enums.Category;
import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "mini_buy")
public class MiniBuy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mini_buy_id",nullable = false)
    private long id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private int targetCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BuyStatus status;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private String productImg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;




}
