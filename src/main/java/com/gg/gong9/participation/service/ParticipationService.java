package com.gg.gong9.participation.service;

import com.gg.gong9.global.exception.exceptions.minibuy.MiniBuyException;
import com.gg.gong9.global.exception.exceptions.minibuy.MiniBuyExceptionMessage;
import com.gg.gong9.global.exception.exceptions.participation.ParticipationException;
import com.gg.gong9.global.exception.exceptions.participation.ParticipationExceptionMessage;
import com.gg.gong9.minibuy.entity.MiniBuy;
import com.gg.gong9.minibuy.repository.MiniBuyRepository;
import com.gg.gong9.participation.controller.dto.ParticipationCreateRequestDto;
import com.gg.gong9.participation.controller.dto.ParticipationDetailResponseDto;
import com.gg.gong9.participation.controller.dto.ParticipationListResponse;
import com.gg.gong9.participation.entity.Participation;
import com.gg.gong9.participation.repository.ParticipationRepository;
import com.gg.gong9.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final MiniBuyRepository miniBuyRepository;


    // 소량공구 참여 요청
    @Transactional
    public Participation createParticipation(User user, ParticipationCreateRequestDto requestDto) {

        MiniBuy miniBuy = miniBuyRepository.findById(requestDto.miniBuyId())
                .orElseThrow(()->new MiniBuyException(MiniBuyExceptionMessage.NOT_FOUND_MINI_BUY));

        validateNotAlreadyJoined(user, miniBuy);

        // db 원자적 감소 => ( 인원 감소 & 모집 완료시 상태 변경)
        int updatedRows = miniBuyRepository.tryDecreaseRemainCount(miniBuy.getId());
        if (updatedRows == 0) {
            throw new MiniBuyException(MiniBuyExceptionMessage.PARTICIPATION_FAIL);
        }

        Participation participation = Participation.create(user, miniBuy);
        participationRepository.save(participation);

        return participation;

    }

    // 내가 참여한 공구 목록 조회
    @Transactional(readOnly = true)
    public List<ParticipationListResponse> getParticipationList(User user) {
        return participationRepository.findAllByUserId(user.getId()).stream()
                .map(ParticipationListResponse::from)
                .toList();
    }

    // 참여 상세 조회
    @Transactional(readOnly = true)
    public ParticipationDetailResponseDto getParticipationDetail(User user, Long participationId) {
        Participation participation = getParticipationOrThrow(participationId);
        participation.validateOwner(user);
        return ParticipationDetailResponseDto.from(participation);
    }

    // 참여 취소 요청
    @Transactional
    public void cancelParticipation(User user,Long participationId) {
        Participation participation = getParticipationOrThrow(participationId);
        participation.validateOwner(user);

        // 취소 요청이 가능한 상태인지 확인
        validateParticipationCancelable(participation);
        participation.cancel();
        // 원자적 인원 증가
        int updatedRows = miniBuyRepository.tryIncreaseRemainCount(participation.getMiniBuy().getId());
        if (updatedRows == 0) { // 업데이트 실패
            throw new MiniBuyException(MiniBuyExceptionMessage.CANNOT_INCREASE_REMAIN_COUNT);
        }    }

    // 참여 삭제
    public void deleteParticipation(Long participationId, User user) {
        Participation participation = getParticipationOrThrow(participationId);
        participation.validateOwner(user);
        participationRepository.delete(participation);
    }



    private Participation getParticipationOrThrow(Long participationId) {
        return participationRepository.findById(participationId)
                .orElseThrow(()->new ParticipationException(ParticipationExceptionMessage.NOT_FOUND_PARTICIPATION));
    }

    private void validateNotAlreadyJoined(User userId, MiniBuy miniBuy){
        if(participationRepository.existsByUserAndMiniBuy(userId,miniBuy)){
            throw new ParticipationException(ParticipationExceptionMessage.ALREADY_JOINED_PARTICIPATION);
        }
    }

    private void validateParticipationCancelable(Participation participation) {
        // 이미 취소된 내역이면 예외
        if (participation.isCanceled()) {
            throw new ParticipationException(ParticipationExceptionMessage.ALREADY_CANCELED);
        }
        // 소량 공구의 상태가 모집 중인지
        if (!participation.getMiniBuy().isOpen()) {
            throw new ParticipationException(ParticipationExceptionMessage.MINI_BUY_NOT_OPEN);
        }
    }


    }
