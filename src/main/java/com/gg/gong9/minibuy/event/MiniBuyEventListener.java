package com.gg.gong9.minibuy.event;

import com.gg.gong9.global.exception.exceptions.minibuy.MiniBuyException;
import com.gg.gong9.global.exception.exceptions.minibuy.MiniBuyExceptionMessage;
import com.gg.gong9.notification.sms.controller.SmsNotificationType;
import com.gg.gong9.notification.sms.service.SmsNotificationService;
import com.gg.gong9.participation.repository.ParticipationRepository;
import com.gg.gong9.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MiniBuyEventListener {

    private final SmsNotificationService smsNotificationService;
    private final ParticipationRepository participationRepository;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMiniBuyCompleted(MiniBuyCompletedEvent event){
        log.info(" 리스너 진입확인 miniBuyId={}", event.miniBuyId());

        List<User> participants = participationRepository.findAllUsersByMiniBuyId(event.miniBuyId());
        log.info("참여자 수: {}", participants.size());


        if (!participants.isEmpty()) {
            try {
                smsNotificationService.sendBulkSms(participants, SmsNotificationType.GROUP_BUY_SUCCESS);
                log.info(" 모집 완료. 알림 전송 처리 miniBuyId={}", event.miniBuyId());
            } catch (Exception e) {
                log.error(" 알림 전송 실패 miniBuyId={}", event.miniBuyId(), e);
                throw new MiniBuyException(MiniBuyExceptionMessage.SMS_SEND_FAIL);
            }
        } else {
            log.info("참여자없음. SMS 전송x miniBuyId={}", event.miniBuyId());
        }
    }
}
