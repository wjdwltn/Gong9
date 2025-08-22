package com.gg.gong9.minibuy.entity;

import com.gg.gong9.global.base.BaseEntity;
import com.gg.gong9.global.enums.Category;
import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.global.exception.exceptions.minibuy.MiniBuyException;
import com.gg.gong9.global.exception.exceptions.minibuy.MiniBuyExceptionMessage;
import com.gg.gong9.global.scheduler.StatusUpdatable;
import com.gg.gong9.minibuy.controller.command.MiniBuyUpdateCommand;
import com.gg.gong9.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "mini_buy")
public class MiniBuy extends BaseEntity implements StatusUpdatable {

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
    private int remainCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BuyStatus status;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    private String productImg;
    private String chatLink;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    public static MiniBuy create(String productName,
                                 String productImg,
                                 String description,
                                 int price,
                                 Category category,
                                 int targetCount,
                                 LocalDateTime startAt,
                                 LocalDateTime endAt,
                                 User user) {
        return MiniBuy.builder()
                .productName(productName)
                .productImg(productImg)
                .description(description)
                .price(price)
                .category(category)
                .targetCount(targetCount)
                .remainCount(targetCount)
                .startAt(startAt)
                .endAt(endAt)
                .user(user)
                .status(BuyStatus.BEFORE_START)
                .build();
    }

    public void update(MiniBuyUpdateCommand command) {
        switch (this.status) {
            case BEFORE_START -> updateBeforeStart(command);
            case RECRUITING -> updateWhileRecruiting(command);
            case CANCELED, COMPLETED -> throw new MiniBuyException(MiniBuyExceptionMessage.INVALID_MINI_BUY_UPDATE_STATUS);
        }
    }

    public void updateProductImage(String newImageUrl) {
        this.productImg = newImageUrl;
    }

    public void cancel(){
        this.status = BuyStatus.CANCELED;
    }

    private void updateBeforeStart(MiniBuyUpdateCommand command) {
        this.productName = command.productName();
        this.description = command.description();
        this.price = command.price();
        this.category = command.category();
        this.targetCount = command.targetCount();
        this.startAt = command.startAt();
        this.endAt = command.endAt();
    }

    private void updateWhileRecruiting(MiniBuyUpdateCommand command) {
        this.targetCount = command.targetCount();
        this.description = command.description();
        this.category = command.category();
        this.endAt = command.endAt();
    }

    public void validateOwner(User user) {
        if(!this.user.getId().equals(user.getId())){
            throw new MiniBuyException(MiniBuyExceptionMessage.NO_PERMISSION_MINI_BUY);
        }
    }

    public boolean isOpen() {
        return this.status == BuyStatus.RECRUITING;
    }

    public void shareChatLink(String link) {
        this.chatLink = link;
    }

    public int getJoinedCount() {
        return targetCount - remainCount;
    }

    public void validateCompleted(){
        if(this.status != BuyStatus.COMPLETED){
            throw new MiniBuyException(MiniBuyExceptionMessage.MINI_BUY_NOT_COMPLETED);
        }
    }

    public void validateChatLinkExists() {
        if (this.chatLink == null || this.chatLink.isBlank()) {
            throw new MiniBuyException(MiniBuyExceptionMessage.CHAT_LINK_NOT_FOUND);
        }
    }

    @Override
    public void changeStatus(BuyStatus newStatus) {
        this.status = newStatus;
    }

    @Override
    public void updateStatusIfNeeded(LocalDateTime now) {
        if (status == BuyStatus.BEFORE_START && startAt != null && now.isAfter(startAt)) {
            changeStatus(BuyStatus.RECRUITING);
        } else if (status == BuyStatus.RECRUITING && endAt != null && now.isAfter(endAt)) {
            changeStatus(BuyStatus.CANCELED);
        }
    }

}
