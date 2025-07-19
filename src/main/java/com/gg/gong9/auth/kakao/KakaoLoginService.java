package com.gg.gong9.auth.kakao;

import com.gg.gong9.auth.kakao.dto.KakaoProfileResponse;
import com.gg.gong9.auth.kakao.dto.KakaoTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoLoginService implements SocialLoginService {

    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoProfileClient kakaoProfileClient;

    @Value("${spring.security.oauth2.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.kakao.redirect-uri}")
    private String redirectUri;


    @Override
    public SocialLogin getSocialLoginInfo(String accessCode) {
        Map<String, String> param = Map.of(
                "grant_type", "authorization_code",
                "client_id", clientId,
                "client_secret", clientSecret,
                "redirect_uri", redirectUri,
                "code", accessCode
        );
        //access_token 얻는 uri
        KakaoTokenResponse tokenResponse = kakaoAuthClient.getToken(param);

        //
        String accessToken = "Bearer " + tokenResponse.access_token();
        KakaoProfileResponse profile = kakaoProfileClient.getUserInfo(accessToken);

        return SocialLogin.builder()
                .provider("kakao")
                .providerId(profile.id())
                .email(profile.kakao_account().email())
                .nickname(profile.kakao_account().profile().nickname() + "_kakao")
                .profileImageUrl(profile.kakao_account().profile().profile_image_url())
                .build();
    }
}
