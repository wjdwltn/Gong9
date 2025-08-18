package com.gg.gong9.order.repository;

import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.order.entity.Order;
import com.gg.gong9.order.entity.OrderStatus;
import com.gg.gong9.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Long userId);

    Optional<Order> findByIdAndIsDeletedFalse(Long id);

    boolean existsByUserAndGroupBuyAndStatusNot(User user, GroupBuy groupBuy, OrderStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.status = :newStatus WHERE o.groupBuy.id = :groupBuyId AND o.status <> :newStatus")
    void updateStatusByGroupBuyId(@Param("newStatus") OrderStatus newStatus, @Param("groupBuyId") Long groupBuyId);

    @Query("select distinct o.user from Order o where o.groupBuy.id = :groupBuyId and o.isDeleted = false and o.status <> :cancelledStatus")
    List<User> findDistinctUsersByOrdersGroupBuyIdAndStatusNotCancelled(
            @Param("groupBuyId") Long groupBuyId,
            @Param("cancelledStatus") OrderStatus cancelledStatus);

}
 