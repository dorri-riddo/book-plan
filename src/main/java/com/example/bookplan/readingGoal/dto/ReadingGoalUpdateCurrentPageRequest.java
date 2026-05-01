package com.example.bookplan.readingGoal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "현재 페이지 수정 요청")
public class ReadingGoalUpdateCurrentPageRequest {
    @Schema(description = "현재 읽은 페이지", example = "150")
    @NotNull
    @PositiveOrZero
    private Integer currentPage;
}
