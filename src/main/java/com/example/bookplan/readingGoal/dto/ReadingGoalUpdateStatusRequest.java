package com.example.bookplan.readingGoal.dto;

import com.example.bookplan.readingGoal.ReadingGoalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "독서 목표 상태 수정 요청")
public class ReadingGoalUpdateStatusRequest {
    @Schema(description = "변경할 상태 (IN_PROGRESS, COMPLETED, ABANDONED)", example = "COMPLETED")
    @NotNull
    private ReadingGoalStatus status;
}
