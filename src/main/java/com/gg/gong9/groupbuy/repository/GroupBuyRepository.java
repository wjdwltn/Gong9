package com.gg.gong9.groupbuy.repository;

import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.global.enums.Category;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupBuyRepository extends JpaRepository <GroupBuy, Long> {
    List<GroupBuy> findByUserId(Long userId);
    List<GroupBuy> findByProductCategoryAndStatus(Category category, BuyStatus status);
    List<GroupBuy> findAllByStatusOrderByEndAtAsc(BuyStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "javax.persistence.lock.timeout", value = "2000") // 3000ms = 3초
    })
    @Query("SELECT g FROM GroupBuy g WHERE g.id = :id")
    Optional<GroupBuy> findByIdWithPessimisticLock(@Param("id") Long id);
}
