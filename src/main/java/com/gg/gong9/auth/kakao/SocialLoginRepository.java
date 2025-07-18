package com.gg.gong9.auth.kakao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {
    //void save(String provider, String providerId, String userId );
    boolean existsByProviderAndProviderId(String provider, Long providerId);
}
