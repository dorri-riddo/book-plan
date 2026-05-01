package com.example.bookplan.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "회원가입 요청")
public class UserCreateRequest {
    @Schema(description = "이름", example = "홍길동")
    @NotBlank
    @Size(max = 30)
    private String name;

    @Schema(description = "이메일", example = "user@example.com")
    @NotBlank
    @Email
    @Size(max = 50)
    private String email;

    @Schema(description = "닉네임", example = "책벌레")
    @NotBlank
    @Size(max = 30)
    private String nickName;

    @Schema(description = "비밀번호", example = "password123")
    @NotBlank
    @Size(min = 5)
    private String password;
}
