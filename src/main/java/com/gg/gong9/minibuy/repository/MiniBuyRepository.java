package com.gg.gong9.minibuy.repository;

import com.gg.gong9.global.enums.Category;
import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.minibuy.entity.MiniBuy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MiniBuyRepository extends JpaRepository<MiniBuy,Long> {
    List<MiniBuy> findByCategory(Category category);
    List<MiniBuy> findAllByStatusOrderByEndAtAsc(BuyStatus status);
    List<MiniBuy> findByUserId(Long userId);
}
