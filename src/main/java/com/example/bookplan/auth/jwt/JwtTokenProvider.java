package com.example.bookplan.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";
    private final SecretKey key;
    private final long accessValidity;
    private final long refreshValidity;

    public JwtTokenProvider(JwtProperties prop) {
        this.key = Keys.hmacShaKeyFor(prop.getSecret().getBytes(StandardCharsets.UTF_8));
        this.accessValidity = prop.getAccessTokenValidity().toMillis();
        this.refreshValidity = prop.getRefreshTokenValidity().toMillis();
    }

    public String createAccessToken(Long userId) {
        return buildToken(userId, TYPE_ACCESS, accessValidity);
    }

    public String createRefreshToken(Long userId) {
        return buildToken(userId, TYPE_REFRESH, refreshValidity);
    }

    private String buildToken(Long userId, String type, long validityMs) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId.toString())
                .claim(CLAIM_TYPE, type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + validityMs))
                .signWith(key)
                .compact();
    }

    public boolean validate(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            // TODO 로깅 추가 (만료/위조/형식 등 원인 구분 필요)
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Long userId = Long.parseLong(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(
                userId,                    // principal — 컨트롤러에서 꺼낼 값
                null,                      // credentials — JWT는 비밀번호 안 씀
                Collections.emptyList()    // authorities — 권한, MVP에선 빈 리스트
        );
    }
}
