package com.gg.gong9.groupbuy.controller.command;

import java.time.LocalDateTime;

public record GroupBuyUpdateCommand(
        int totalQuantity,
        int limitQuantity,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int paidQuantity

) {
}
