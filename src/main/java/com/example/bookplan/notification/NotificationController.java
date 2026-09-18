package com.example.bookplan.notification;

import com.example.bookplan.notification.dto.DeviceRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification", description = "알림 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("notifications")
public class NotificationController {
    private final NotificationService service;

    @Operation(summary = "푸시 기기 등록",
            description = "현재 기기의 FCM 토큰과 매일 알림 수신 여부를 등록합니다. 로그아웃하면 자동으로 해제됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "등록 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "등록되지 않은 기기")
    })
    @PutMapping("device")
    public ResponseEntity<Void> registerDevice(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody DeviceRegisterRequest request) {
        service.registerDevice(userId, request);
        return ResponseEntity.noContent().build();
    }
}
