package com.example.bookplan.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "로그인 요청")
public class LogInRequest {
    @Schema(description = "이메일", example = "user@example.com")
    @NotBlank
    @Email
    @Size(max = 50)
    private String email;

    @Schema(description = "비밀번호", example = "password123")
    @NotBlank
    @Size(min = 5)
    private String password;
}
