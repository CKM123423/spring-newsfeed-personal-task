package com.sparta.areadevelopment.repository;

import com.sparta.areadevelopment.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByKey(String key);
    Optional<RefreshToken> findByValue(String value);

}
