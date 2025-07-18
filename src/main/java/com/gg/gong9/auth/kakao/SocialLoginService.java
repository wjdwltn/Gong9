package com.gg.gong9.auth.kakao;

import com.gg.gong9.user.entity.User;

public interface SocialLoginService {
    SocialLogin getSocialLoginInfo(String accessCode);
}
