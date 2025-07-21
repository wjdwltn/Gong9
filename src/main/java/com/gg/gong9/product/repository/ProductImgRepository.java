package com.gg.gong9.product.repository;

import com.gg.gong9.product.entity.ProductImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductImgRepository extends JpaRepository<ProductImg, Long> {
    List<ProductImg> findAllByProductId(Long productId);

}
