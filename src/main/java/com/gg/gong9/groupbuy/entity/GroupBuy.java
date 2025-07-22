package com.gg.gong9.groupbuy.entity;

import com.gg.gong9.global.base.BaseEntity;
import com.gg.gong9.global.exception.exceptions.groupbuy.GroupBuyException;
import com.gg.gong9.groupbuy.controller.dto.GroupBuyCreateRequestDto;
import com.gg.gong9.groupbuy.controller.command.GroupBuyUpdateCommand;
import com.gg.gong9.product.entity.Product;
import com.gg.gong9.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.gg.gong9.global.exception.exceptions.groupbuy.GroupBuyExceptionMessage.INVALID_TOTAL_QUANTITY;


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


    private GroupBuy(int totalQuantity, int limitQuantity, LocalDateTime startAt, LocalDateTime endAt, Status status, Product product, User user) {
        this.totalQuantity = totalQuantity;
        this.limitQuantity = limitQuantity;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
        this.product = product;
        this.user = user;
    }


    public static GroupBuy create(GroupBuyCreateRequestDto dto, Product product, User user) {
        return new GroupBuy(
                dto.totalQuantity(),
                dto.limitQuantity(),
                dto.startAt(),
                dto.endAt(),
                Status.BEFORE_START,
                product,
                user
        );
    }

    public void update(GroupBuyUpdateCommand command) {
        switch (this.status) {
            case BEFORE_START -> updateBeforeStart(command);
            case RECRUITING -> updateRecruiting(command);
            case COMPLETED, CANCELED -> throw new IllegalStateException("모집 완료 및 취소된 공구건 수정 불가");
        }
    }

    private void updateBeforeStart(GroupBuyUpdateCommand command) {
        this.totalQuantity = command.totalQuantity();
        this.limitQuantity = command.limitQuantity();
        this.startAt = command.startAt();
        this.endAt = command.endAt();
    }

    private void updateRecruiting(GroupBuyUpdateCommand command) {
        if (command.totalQuantity() < command.paidQuantity()) {
            throw new GroupBuyException(INVALID_TOTAL_QUANTITY);
        }
        this.totalQuantity = command.totalQuantity();
        this.limitQuantity = command.limitQuantity();
        this.endAt = command.endAt();
    }

    public void updateStatus(){
        LocalDateTime now = LocalDateTime.now();
        if (status == Status.BEFORE_START && now.isAfter(startAt)) {
            this.status = Status.RECRUITING;
        } else if (status == Status.RECRUITING && now.isAfter(endAt)) {
            this.status = Status.COMPLETED;
        }
    }

    public void cancel(){
        this.status = Status.CANCELED;
    }
}
