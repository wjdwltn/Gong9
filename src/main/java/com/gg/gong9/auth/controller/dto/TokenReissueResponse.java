package com.gg.gong9.auth.controller.dto;

public record TokenReissueResponse(String newAccessToken, String newRefreshToken) {
}
