package com.example.bookplan.auth;

import com.example.bookplan.auth.dto.LogInRequest;
import com.example.bookplan.auth.dto.LogInResponse;
import com.example.bookplan.auth.dto.LogOutRequest;
import com.example.bookplan.auth.dto.RefreshRequest;
import com.example.bookplan.auth.exception.InvalidRefreshTokenException;
import com.example.bookplan.auth.exception.NotFoundEmailException;
import com.example.bookplan.auth.exception.WrongPasswordException;
import com.example.bookplan.auth.jwt.JwtTokenProvider;
import com.example.bookplan.user.User;
import com.example.bookplan.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private static final long AUTO_LOGIN_VALIDITY = Duration.ofDays(30).toMillis();
    private static final Duration GRACE_PERIOD = Duration.ofSeconds(30);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    public LogInResponse logIn(LogInRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundEmailException(request.getEmail()));

        boolean passwordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (passwordMatch == false) {
            throw new WrongPasswordException();
        }

        return issueTokens(user.getId(), request.getDeviceId(), request.isAutoLogin());
    }

    public LogInResponse issueTokens(Long userId, String deviceId, boolean autoLogin) {
        long refreshValidityMs = autoLogin
                ? AUTO_LOGIN_VALIDITY
                : jwtTokenProvider.getRefreshValidity();

        String accessToken = jwtTokenProvider.createAccessToken(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId, refreshValidityMs);
        Instant refreshTokenExpiredAt = Instant.now().plusMillis(refreshValidityMs);

        Optional<Token> existingToken = tokenRepository.findByUserIdAndDeviceId(userId, deviceId);

        if (existingToken.isPresent()) {
            existingToken.get().updateTokens(accessToken, refreshToken,
                    refreshTokenExpiredAt, autoLogin);
        } else {
            Token token = new Token(userId, deviceId,
                    accessToken, refreshToken, refreshTokenExpiredAt, autoLogin);
            tokenRepository.save(token);
        }

        return LogInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void logOut(Long userId, LogOutRequest request) {
        tokenRepository.deleteByUserIdAndDeviceId(userId, request.getDeviceId());
    }

    public LogInResponse refresh(RefreshRequest request) {
        String rawToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateRefreshToken(rawToken)) {
            throw new InvalidRefreshTokenException();
        }

        // 1. 현재 리프레시 토큰으로 조회
        Optional<Token> tokenOpt = tokenRepository.findByRefreshToken(rawToken);

        if (tokenOpt.isPresent()) {
            Token token = tokenOpt.get();

            if (token.getRefreshTokenExpiredAt().isBefore(Instant.now())) {
                throw new InvalidRefreshTokenException();
            }

            long refreshValidityMs = token.isAutoLogin()
                    ? AUTO_LOGIN_VALIDITY
                    : jwtTokenProvider.getRefreshValidity();

            String newAccessToken = jwtTokenProvider.createAccessToken(token.getUserId());
            String newRefreshToken = jwtTokenProvider.createRefreshToken(token.getUserId(), refreshValidityMs);
            Instant newRefreshTokenExpiredAt = Instant.now().plusMillis(refreshValidityMs);
            Instant gracePeriodExpiredAt = Instant.now().plus(GRACE_PERIOD);

            token.rotateRefreshToken(newAccessToken, newRefreshToken,
                    newRefreshTokenExpiredAt, gracePeriodExpiredAt);

            return LogInResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();
        }

        // 2. Grace period: 이전 리프레시 토큰으로 조회
        Optional<Token> graceOpt = tokenRepository.findByPreviousRefreshToken(rawToken);

        if (graceOpt.isPresent()) {
            Token token = graceOpt.get();
            if (token.getPreviousRefreshTokenExpiredAt() != null
                    && token.getPreviousRefreshTokenExpiredAt().isAfter(Instant.now())) {
                // 이미 갱신된 현재 토큰을 그대로 반환
                return LogInResponse.builder()
                        .accessToken(token.getAccessToken())
                        .refreshToken(token.getRefreshToken())
                        .build();
            }
        }

        throw new InvalidRefreshTokenException();
    }
}
