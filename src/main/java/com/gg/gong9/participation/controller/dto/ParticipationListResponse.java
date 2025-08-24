package com.gg.gong9.participation.controller.dto;

import com.gg.gong9.minibuy.entity.MiniBuy;
import com.gg.gong9.participation.entity.Participation;

import java.time.LocalDateTime;

public record ParticipationListResponse(
        Long participationId,
        String participationStatus,
        LocalDateTime participationAt,

        Long miniBuyId,
        String miniBuyProductName,
        int miniBuyPrice,
        String miniBuyImg,
        String MiniBuyStatus,
        LocalDateTime MiniBuyStartAt,
        LocalDateTime MiniBuyEndAt
) {
    public static ParticipationListResponse from(Participation participation) {
        MiniBuy miniBuy = participation.getMiniBuy();

        return new ParticipationListResponse(
                participation.getId(),
                participation.getStatus().toString(),
                participation.getCreatedAt(),

                miniBuy.getId(),
                miniBuy.getProductName(),
                miniBuy.getPrice(),
                miniBuy.getProductImg(),
                miniBuy.getStatus().toString(),
                miniBuy.getStartAt(),
                miniBuy.getEndAt()
        );
    }

}
