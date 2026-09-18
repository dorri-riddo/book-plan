package com.example.bookplan.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUserIdAndDeviceId(Long userId, String deviceId);
    Optional<Token> findByRefreshToken(String refreshToken);
    Optional<Token> findByPreviousRefreshToken(String previousRefreshToken);
    void deleteByUserIdAndDeviceId(Long userId, String deviceId);
    void deleteByUserId(Long userId);
    boolean existsByUserIdAndAccessToken(Long userId, String accessToken);
    List<Token> findAllByFcmTokenIsNotNullAndNotificationEnabledIsTrue();

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Token t set t.fcmToken = null where t.fcmToken in :fcmTokens")
    int clearFcmTokens(@Param("fcmTokens") List<String> fcmTokens);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Token t set t.fcmToken = null where t.deviceId = :deviceId and t.userId <> :userId")
    int releaseDeviceFromOtherUsers(@Param("deviceId") String deviceId, @Param("userId") Long userId);
}
