package com.gg.gong9.global.scheduler;

import com.gg.gong9.groupbuy.service.GroupBuyService;
import com.gg.gong9.minibuy.service.MiniBuyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class BuyStatusScheduler {

    private final GroupBuyService groupBuyService;
    private final MiniBuyService miniBuyService;

    @Scheduled(fixedRate = 30000)
    public void updateStatus() {
        log.info(" 상태 업데이트 스케줄러 실행 !!: {} ", LocalDateTime.now());

        groupBuyService.updateAllGroupBuyStatus();
        miniBuyService.updateAllMiniBuyStatus();

    }
}
