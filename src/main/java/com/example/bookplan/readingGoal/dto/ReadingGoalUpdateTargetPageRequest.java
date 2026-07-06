package com.example.bookplan.readingGoal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "목표 분량 수정 요청")
public class ReadingGoalUpdateTargetPageRequest {
    @Schema(description = "목표 페이지", example = "300")
    @NotNull
    @Positive
    private Integer targetPage;
}
