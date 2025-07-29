package com.gg.gong9.product.entity;

import com.gg.gong9.global.base.BaseEntity;
import com.gg.gong9.global.enums.Category;
import com.gg.gong9.product.controller.dto.ProductCreateRequestDto;
import com.gg.gong9.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private long id;

    private String productName;

    private String description;

    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImg> productImgs = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Product(String productName, String description, int price, Category category, User user) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.category = category;
        this.user = user;
    }

    public static Product create(ProductCreateRequestDto dto, User user) {
        return new Product(
                dto.productName(),
                dto.description(),
                dto.price(),
                dto.category(),
                user
        );
    }

    public void update( String productName, String description, Integer price, Category category) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.category = category;
    }

}

