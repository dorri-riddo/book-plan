package com.example.bookplan.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUserIdAndDeviceId(Long userId, String deviceId);
    Optional<Token> findByRefreshToken(String refreshToken);
    Optional<Token> findByPreviousRefreshToken(String previousRefreshToken);
    void deleteByUserIdAndDeviceId(Long userId, String deviceId);
    void deleteByUserId(Long userId);
    boolean existsByUserIdAndAccessToken(Long userId, String accessToken);
}
