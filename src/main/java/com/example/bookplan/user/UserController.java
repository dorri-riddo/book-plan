package com.example.bookplan.user;

import com.example.bookplan.auth.dto.LogInResponse;
import com.example.bookplan.user.dto.UserCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "회원 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {
    private final UserService service;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록하고, 곧바로 로그인 상태가 되도록 토큰을 발급합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
        @ApiResponse(responseCode = "409", description = "이메일 중복")
    })
    @PostMapping
    public ResponseEntity<LogInResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @Operation(summary = "회원 탈퇴", description = "인증된 사용자를 탈퇴 처리합니다. (users 로우 하드 삭제)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "탈퇴 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @DeleteMapping
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal Long userId) {
        service.withdraw(userId);
        return ResponseEntity.ok().build();
    }
}
