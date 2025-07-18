package com.gg.gong9.global.utils.kakao;

import org.springframework.beans.factory.annotation.Value;

public class KakaoUtil{

    @Value("{spring.kakao.auth.client-id}")
    private String client;
    @Value("{spring.kakao.auth.redirect-uri}")
    private String redirect;
}