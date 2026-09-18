package com.example.bookplan.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "푸시 알림 기기 등록 요청")
public class DeviceRegisterRequest {
    @Schema(description = "기기 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotBlank
    private String deviceId;

    @Schema(description = "FCM 등록 토큰", example = "fsK3q...:APA91bH...")
    @NotBlank
    private String fcmToken;

    @Schema(description = "매일 알림 수신 여부", example = "true")
    @NotNull
    private Boolean notificationEnabled;
}
