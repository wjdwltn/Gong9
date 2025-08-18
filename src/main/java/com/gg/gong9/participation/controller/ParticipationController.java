package com.gg.gong9.participation.controller;

import com.gg.gong9.global.security.jwt.CustomUserDetails;
import com.gg.gong9.participation.controller.dto.*;
import com.gg.gong9.participation.entity.Participation;
import com.gg.gong9.participation.service.ParticipationService;
import com.gg.gong9.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/participation")
public class ParticipationController {

    private final ParticipationService participationService;

    // 소량 공구 참여 요청
    @PostMapping
    public ResponseEntity<ParticipationCreateResponse> createParticipation(
            @Valid @RequestBody ParticipationCreateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        Participation participation = participationService.createParticipation(user, requestDto);
        return ResponseEntity.ok(new ParticipationCreateResponse(participation.getId(), "소량공구 참여 요청이 완료되었습니다."));
    }

    // 내가 참여한 소량 공구 목록 조회
    @GetMapping("/me")
    public ResponseEntity<List<ParticipationListResponse>> getMyParticipation(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        List<ParticipationListResponse> response = participationService.getParticipationList(user);
        return ResponseEntity.ok(response);
    }

    // 참여 상세 조회
    @GetMapping("/{participationId}")
    public ResponseEntity<ParticipationDetailResponseDto> getParticipation(
            @PathVariable("participationId") Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        ParticipationDetailResponseDto detail = participationService.getParticipationDetail(user, participationId);
        return ResponseEntity.ok(detail);
    }

    // 참여 취소 요청
    @PutMapping("/{participationId}/cancel")
    public ResponseEntity<ParticipationResponse> cancelParticipation(
            @PathVariable("participationId") Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        participationService.cancelParticipation(user, participationId);
        return ResponseEntity.ok(new ParticipationResponse("참여가 취소되었습니다."));
    }

    // 참여 삭제
    @DeleteMapping("/{participationId}")
    public ResponseEntity<ParticipationResponse> deleteParticipation(
            @PathVariable("participationId") Long participationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        participationService.deleteParticipation(participationId, userDetails.getUser());
        return ResponseEntity.ok(new ParticipationResponse("해당 소량공구 참여가 삭제되었습니다."));
    }
}
