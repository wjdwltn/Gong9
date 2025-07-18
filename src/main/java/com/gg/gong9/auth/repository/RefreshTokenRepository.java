package com.gg.gong9.auth.repository;

import java.util.Optional;

public interface RefreshTokenRepository {
    void saveRefreshToken(String email, String refreshToken);

    Optional<String> getRefreshToken(String email);

    void deleteRefreshToken(String email);
}
