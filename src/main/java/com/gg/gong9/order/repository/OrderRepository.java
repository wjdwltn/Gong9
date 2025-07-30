package com.gg.gong9.order.repository;

import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.order.entity.Order;
import com.gg.gong9.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Long userId);

    List<Order> findAllByGroupBuy(GroupBuy groupBuy);

    Optional<Order> findByIdAndIsDeletedFalse(Long id);

    boolean existsByUserAndGroupBuy(User user, GroupBuy groupBuy);
}
 