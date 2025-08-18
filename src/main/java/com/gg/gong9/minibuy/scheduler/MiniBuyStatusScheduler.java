package com.gg.gong9.minibuy.scheduler;

import com.gg.gong9.minibuy.service.MiniBuyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Slf4j
@Component
@RequiredArgsConstructor
public class MiniBuyStatusScheduler {

    private final MiniBuyService miniBuyService;

    @Scheduled(fixedRate = 30000) // 30초
    public void updateMiniBuyStatusTask() {
        log.info("스케줄러 실행: {}", LocalDateTime.now());
        miniBuyService.updateAllMiniBuyStatus();
    }
}