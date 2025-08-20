package com.gg.gong9.global.scheduler;

import com.gg.gong9.global.enums.BuyStatus;

import java.time.LocalDateTime;

public interface StatusUpdatable {
    BuyStatus getStatus();
    LocalDateTime getStartAt();
    LocalDateTime getEndAt();
    void changeStatus(BuyStatus newStatus);
    void updateStatusIfNeeded(LocalDateTime now);
}
