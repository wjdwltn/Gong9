package com.gg.gong9.minibuy.controller.dto;

import com.gg.gong9.participation.entity.Participation;

public record ParticipantInfoResponseDto(
        Long userId,
        String username
) {
    public static ParticipantInfoResponseDto from(Participation participation){
        return new ParticipantInfoResponseDto(
                participation.getUser().getId(),
                participation.getUser().getUsername()
        );
    }
}