package com.example.bookplan.user;

import com.example.bookplan.user.dto.UserCreateRequest;
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

@Tag(name = "User", description = "회원 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {
    private final UserService service;

    // 1. 회원 탈퇴
    // 2. 본인 정보 조회
    // 3. 본인 정보 수정
    // 4. 이메일 인증
    // 5. 회원가입은 user 인가 auth 인가... 탈퇴도 마찬가지인데 흠...

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
        @ApiResponse(responseCode = "409", description = "이메일 중복")
    })
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody UserCreateRequest request) {
        User user = service.create(request);
        return ResponseEntity.ok(user);
    }
}
