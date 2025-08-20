package com.gg.gong9.minibuy.repository;

import com.gg.gong9.global.enums.Category;
import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.minibuy.entity.MiniBuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MiniBuyRepository extends JpaRepository<MiniBuy,Long> {
    List<MiniBuy> findByCategory(Category category);
    List<MiniBuy> findAllByStatusOrderByEndAtAsc(BuyStatus status);
    List<MiniBuy> findByUserId(Long userId);
    // 참여 요청 (남은 인원 감소 & 모집완료 상태 변경(남은 인원 0일 때))
    @Modifying
    @Query("""
        UPDATE MiniBuy m
        SET m.status = CASE WHEN m.remainCount = 1 THEN 'COMPLETED' ELSE m.status END,
            m.remainCount = m.remainCount - 1
        WHERE m.id = :miniBuyId
          AND m.remainCount > 0
          AND m.status = 'RECRUITING'
    """)
    int tryDecreaseRemainCount(@Param("miniBuyId") Long miniBuyId);

    // 참여 취소 (남은 인원 증가)
    @Modifying
    @Query("""
    UPDATE MiniBuy m
    SET m.remainCount = m.remainCount + 1
    WHERE m.id = :miniBuyId
      AND m.remainCount < m.targetCount
""")
    int tryIncreaseRemainCount(@Param("miniBuyId") Long miniBuyId);

    // 스케줄러 조회
    @Query("""
        SELECT m FROM MiniBuy m
        WHERE m.status IN('BEFORE_START','RECRUITING')
    """)
    List<MiniBuy> findAllToUpdateStatus(@Param("now") LocalDateTime now);

}
