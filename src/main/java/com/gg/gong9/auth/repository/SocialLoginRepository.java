package com.gg.gong9.auth.repository;

import com.gg.gong9.auth.kakao.SocialLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {
    boolean existsByProviderAndProviderId(String provider, Long providerId);
}
