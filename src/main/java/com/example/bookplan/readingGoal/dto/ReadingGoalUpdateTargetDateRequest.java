package com.example.bookplan.readingGoal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@Schema(description = "목표 날짜 수정 요청")
public class ReadingGoalUpdateTargetDateRequest {
    @Schema(description = "목표 완료 날짜", example = "2026-07-31T00:00:00Z")
    @NotNull
    @FutureOrPresent
    private Instant targetDate;
}
