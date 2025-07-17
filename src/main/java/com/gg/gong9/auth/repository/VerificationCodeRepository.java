package com.gg.gong9.auth.repository;

import java.util.Optional;

public interface VerificationCodeRepository {

    void saveCode(String key, String code, long ttlSeconds);

    Optional<String> getCode(String key);

    void deleteCode(String key);
}
