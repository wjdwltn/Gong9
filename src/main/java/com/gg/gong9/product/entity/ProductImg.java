package com.gg.gong9.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_img")
public class ProductImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_img_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private String productImageUrl;

    public static ProductImg createProductImg(Product product, String productImageUrl) {
        ProductImg productImg = new ProductImg();
        productImg.product = product;
        productImg.productImageUrl = productImageUrl;
        return productImg;
    }
}
