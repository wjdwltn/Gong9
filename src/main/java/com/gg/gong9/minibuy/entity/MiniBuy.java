package com.gg.gong9.minibuy.entity;

import com.gg.gong9.global.base.BaseEntity;
import com.gg.gong9.global.enums.Category;
import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.global.exception.exceptions.minibuy.MiniBuyException;
import com.gg.gong9.global.exception.exceptions.minibuy.MiniBuyExceptionMessage;
import com.gg.gong9.minibuy.controller.command.MiniBuyUpdateCommand;
import com.gg.gong9.minibuy.controller.dto.MiniBuyCreateRequestDto;
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


    public MiniBuy(String productName, String productImg, String description, int price, Category category, int targetCount, LocalDateTime startAt, LocalDateTime endAt, User user) {
        this.productName = productName;
        this.productImg = productImg;
        this.description = description;
        this.price = price;
        this.category = category;
        this.targetCount = targetCount;
        this.startAt = startAt;
        this.endAt = endAt;
        this.user = user;
        this.status = BuyStatus.BEFORE_START;
    }

    public static MiniBuy create(MiniBuyCreateRequestDto dto, String imageUrl, User user) {
        return new MiniBuy(
                dto.productName(),
                imageUrl,
                dto.description(),
                dto.price(),
                dto.category(),
                dto.targetCount(),
                dto.startAt(),
                dto.endAt(),
                user
        );
    }

    @PrePersist
    protected void prePersist() {
        if (this.status == null) {
            this.status = BuyStatus.BEFORE_START;
        }
    }

    public void update(MiniBuyUpdateCommand command) {
        switch (this.status) {
            case BEFORE_START -> updateAllFields(command);
            case RECRUITING -> updateRecruitingFields(command);
            case CANCELED, COMPLETED -> throw new MiniBuyException(MiniBuyExceptionMessage.INVALID_MINI_BUY_UPDATE_STATUS);
        }
    }

    public void updateProductImage(String newImageUrl) {
        this.productImg = newImageUrl;
    }

    public void cancel(){
        this.status = BuyStatus.CANCELED;
    }

    private void updateAllFields(MiniBuyUpdateCommand command) {
        this.productName = command.productName();
        this.description = command.description();
        this.price = command.price();
        this.category = command.category();
        this.targetCount = command.targetCount();
        this.startAt = command.startAt();
        this.endAt = command.endAt();
    }

    private void updateRecruitingFields(MiniBuyUpdateCommand command) {
        this.targetCount = command.targetCount();
        this.description = command.description();
        this.category = command.category();
        this.endAt = command.endAt();
    }


}
