package com.gg.gong9.minibuy.controller.command;

import com.gg.gong9.global.enums.Category;

import java.time.LocalDateTime;

public record MiniBuyUpdateCommand(
        String productName,
        String description,
        int price,
        Category category,
        int targetCount,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
}
