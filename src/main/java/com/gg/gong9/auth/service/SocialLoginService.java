package com.gg.gong9.auth.service;

import com.gg.gong9.auth.kakao.SocialLogin;

public interface SocialLoginService {
    SocialLogin getSocialLoginInfo(String accessCode);
}
