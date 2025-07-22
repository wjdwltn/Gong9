package com.gg.gong9.auth.kakao.dto;

public record KakaoProfileResponse(
        Long id,  // Provider ID
        KakaoAccount kakao_account
) {
    public record KakaoAccount(
            String email,
            Profile profile
    ) {}

    public record Profile(
            String nickname,
            String profile_image_url
    ) {}
}