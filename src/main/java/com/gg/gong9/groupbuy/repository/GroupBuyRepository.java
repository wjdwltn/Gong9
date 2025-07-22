package com.gg.gong9.groupbuy.repository;

import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.groupbuy.entity.Status;
import com.gg.gong9.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupBuyRepository extends JpaRepository <GroupBuy, Long> {
    List<GroupBuy> findByUserId(Long userId);
    List<GroupBuy> findByProductCategory(Category category);
    List<GroupBuy> findAllByStatusOrderByEndAtAsc(Status status);
}
