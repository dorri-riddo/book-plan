package com.example.bookplan.auth;

import com.example.bookplan.auth.dto.LogInRequest;
import com.example.bookplan.auth.dto.LogInResponse;
import com.example.bookplan.auth.dto.RefreshRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {
    private final AuthService service;

    // 추후 개발
    // 1. 로그아웃

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰으로 새 액세스 토큰과 리프레시 토큰을 발급합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰")
    })
    @PostMapping("refresh")
    public ResponseEntity<LogInResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        LogInResponse response = service.refresh(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("logIn")
    public ResponseEntity<LogInResponse> logIn(@Valid @RequestBody LogInRequest request) {
        LogInResponse response = service.logIn(request);
        return ResponseEntity.ok(response);
    }
}
