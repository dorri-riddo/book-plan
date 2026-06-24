package com.example.bookplan.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health", description = "헬스 체크 API")
@RestController
@RequestMapping("health")
public class HealthController {
    @Operation(summary = "헬스 체크", description = "서버의 헬스 체크를 합니다")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping
    public String health() {
        return "ok";
    }
}
