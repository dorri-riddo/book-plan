package com.example.bookplan.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "리프레쉬 요청")
public class RefreshRequest {
    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    @NotBlank
    private String refreshToken;

    @Schema(description = "기기 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotBlank
    private String deviceId;
}
