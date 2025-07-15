package com.gg.gong9.user.controller.dto;

public record LoginResponse(
        Long userId,
        String accessToken
) {
}
