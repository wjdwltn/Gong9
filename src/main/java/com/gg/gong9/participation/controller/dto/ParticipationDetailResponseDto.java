package com.gg.gong9.participation.controller.dto;

import com.gg.gong9.minibuy.entity.MiniBuy;
import com.gg.gong9.participation.entity.Participation;
import com.gg.gong9.user.entity.User;

import java.time.LocalDateTime;

public record ParticipationDetailResponseDto(
        Long participationId,
        String participationStatus,
        LocalDateTime participationAt,

        Long miniBuyId,
        String miniBuyProductName,
        int miniBuyPrice,
        String miniBuyImg,
        String MiniBuyStatus,
        LocalDateTime MiniBuyStartAt,
        LocalDateTime MiniBuyEndAt,

        Long buyerId,
        String buyerEmail,
        String buyerName
) {
    public static ParticipationDetailResponseDto from(Participation participation) {
        MiniBuy miniBuy = participation.getMiniBuy();
        User user = participation.getUser();

        return new ParticipationDetailResponseDto(
                participation.getId(),
                participation.getStatus().toString(),
                participation.getCreatedAt(),

                miniBuy.getId(),
                miniBuy.getProductName(),
                miniBuy.getPrice(),
                miniBuy.getProductImg(),
                miniBuy.getStatus().toString(),
                miniBuy.getStartAt(),
                miniBuy.getEndAt(),

                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

}
