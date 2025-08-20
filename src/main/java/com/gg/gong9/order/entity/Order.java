package com.gg.gong9.order.entity;

import com.gg.gong9.global.base.BaseEntity;
import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.product.entity.Product;
import com.gg.gong9.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Or;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private long id;

    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_buy_id")
    private GroupBuy groupBuy;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Builder
    public Order(int quantity, OrderStatus status, User user, GroupBuy groupBuy) {
        this.quantity = quantity;
        this.status = status;
        this.user = user;
        this.groupBuy = groupBuy;
    }

    public void cancel(){this.status = OrderStatus.CANCELLED; }

    public void softDelete(){
        this.isDeleted = true;
    }

}
