package com.gg.gong9.product.entity;

import com.gg.gong9.product.controller.dto.ProductCreateRequestDto;
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
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private long id;

    private String productName;

    private String description;

    private int price;

    private String category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImg> productImgs = new ArrayList<>();

    private Product(String productName, String description, int price, String category) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public static Product create(ProductCreateRequestDto dto) {
        if (dto.price() < 0) throw new IllegalArgumentException("가격은 0원 이상이어야 합니다.");
        return new Product(
                dto.productName(),
                dto.description(),
                dto.price(),
                dto.category()
        );
    }

    public void update( String productName, String description, Integer price, String category) {
        if (productName != null) this.productName = productName;
        if (description != null) this.description = description;
        if (price != null) this.price = price;
        if (category != null) this.category = category;
    }

}

