package com.gg.gong9.order.controller.dto;

import com.gg.gong9.order.entity.Order;
import com.gg.gong9.user.entity.User;

import java.util.List;

public record OrderWithCompletion(
        Order order,
        boolean groupBuyCompleted,
        List<User> allUsers
) {
}
