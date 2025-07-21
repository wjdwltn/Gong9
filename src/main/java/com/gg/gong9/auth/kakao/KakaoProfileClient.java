package com.gg.gong9.auth.kakao;

import com.gg.gong9.auth.kakao.dto.KakaoProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoProfileClient", url = "https://kapi.kakao.com")
public interface KakaoProfileClient {
    @GetMapping("/v2/user/me")
    KakaoProfileResponse getUserInfo(@RequestHeader("Authorization") String accessToken);
}
