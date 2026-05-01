package com.example.bookplan.readingGoal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@Schema(description = "독서 목표 등록 요청")
public class ReadingGoalCreateRequest {
    @Schema(description = "책 ID", example = "1")
    @NotNull
    private Long bookId;

    @Schema(description = "목표 완료 날짜", example = "2026-06-30T00:00:00Z")
    @NotNull
    @FutureOrPresent
    private Instant targetDate;

    @Schema(description = "목표 페이지", example = "300")
    @NotNull
    private Integer targetPage;
}
