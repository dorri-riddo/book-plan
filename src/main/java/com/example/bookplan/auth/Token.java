package com.example.bookplan.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "tokens",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "deviceId"}))
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 64)
    private String deviceId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String accessToken;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String refreshToken;

    @Column(nullable = false)
    private Instant refreshTokenExpiredAt;

    @Column(nullable = false)
    private boolean autoLogin;

    @Column(columnDefinition = "TEXT")
    private String previousRefreshToken;

    private Instant previousRefreshTokenExpiredAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public Token(Long userId, String deviceId, String accessToken, String refreshToken,
                 Instant refreshTokenExpiredAt, boolean autoLogin) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiredAt = refreshTokenExpiredAt;
        this.autoLogin = autoLogin;
    }

    public void updateTokens(String accessToken, String refreshToken,
                             Instant refreshTokenExpiredAt, boolean autoLogin) {
        this.previousRefreshToken = null;
        this.previousRefreshTokenExpiredAt = null;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiredAt = refreshTokenExpiredAt;
        this.autoLogin = autoLogin;
    }

    public void rotateRefreshToken(String newAccessToken, String newRefreshToken,
                                   Instant newRefreshTokenExpiredAt, Instant gracePeriodExpiredAt) {
        this.previousRefreshToken = this.refreshToken;
        this.previousRefreshTokenExpiredAt = gracePeriodExpiredAt;
        this.accessToken = newAccessToken;
        this.refreshToken = newRefreshToken;
        this.refreshTokenExpiredAt = newRefreshTokenExpiredAt;
    }
}
