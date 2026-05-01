package com.example.bookplan.readingGoal;

import com.example.bookplan.readingGoal.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ReadingGoal", description = "독서 목표 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("readingGoals")
public class ReadingGoalController {
    private final ReadingGoalService service;

    @Operation(summary = "독서 목표 목록 조회", description = "사용자의 모든 독서 목표를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping
    public ResponseEntity<List<ReadingGoal>> findAll(
            @AuthenticationPrincipal Long userId
    ) {
        List<ReadingGoal> readingGoals = service.findAll(userId);
        return ResponseEntity.ok(readingGoals);
    }

    @Operation(summary = "독서 목표 단건 조회", description = "독서 목표 ID로 특정 목표를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "독서 목표를 찾을 수 없음")
    })
    @GetMapping("/{readingGoalId}")
    public ResponseEntity<ReadingGoal> findOne(
            @Parameter(description = "독서 목표 ID") @PathVariable Long readingGoalId,
            @AuthenticationPrincipal Long userId
    ) {
        ReadingGoal readingGoal = service.findOne(readingGoalId, userId);
        return ResponseEntity.ok(readingGoal);
    }

    @Operation(summary = "일일 목표 페이지 계산", description = "진행 중인 독서 목표들의 일일 읽기 목표 페이지를 계산합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "계산 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/daily-targets")
    public ResponseEntity<List<ReadingGoalCalculatePagesPerDayResponse>> calculatePagesPerDay(
            @AuthenticationPrincipal Long userId
    ) {
        List<ReadingGoalCalculatePagesPerDayResponse> list = service.calculatePagesPerDay(userId);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "독서 목표 등록", description = "새로운 독서 목표를 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "등록 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "책을 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<ReadingGoal> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ReadingGoalCreateRequest request) {
        ReadingGoal readingGoal = service.create(request, userId);
        return ResponseEntity.ok(readingGoal);
    }

    @Operation(summary = "현재 페이지 수정", description = "독서 목표의 현재 읽은 페이지를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "독서 목표를 찾을 수 없음")
    })
    @PutMapping("/currentPage/{readingGoalId}")
    public ResponseEntity<ReadingGoal> updateCurrentPage(
            @Parameter(description = "독서 목표 ID") @PathVariable Long readingGoalId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ReadingGoalUpdateCurrentPageRequest request
            ) {
        ReadingGoal readingGoal = service.updateCurrentPage(request, readingGoalId, userId);
        return ResponseEntity.ok(readingGoal);
    }

    @Operation(summary = "목표 날짜 수정", description = "독서 목표의 목표 완료 날짜를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "독서 목표를 찾을 수 없음")
    })
    @PutMapping("/targetDate/{readingGoalId}")
    public ResponseEntity<ReadingGoal> updateTargetDate(
            @Parameter(description = "독서 목표 ID") @PathVariable Long readingGoalId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ReadingGoalUpdateTargetDateRequest request
    ) {
        ReadingGoal readingGoal = service.updateTargetDate(request, readingGoalId, userId);
        return ResponseEntity.ok(readingGoal);
    }

    @Operation(summary = "상태 수정", description = "독서 목표의 상태를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "독서 목표를 찾을 수 없음")
    })
    @PutMapping("/status/{readingGoalId}")
    public ResponseEntity<ReadingGoal> updateStatus(
            @Parameter(description = "독서 목표 ID") @PathVariable Long readingGoalId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ReadingGoalUpdateStatusRequest request
    ) {
        ReadingGoal readingGoal = service.updateStatus(request, readingGoalId, userId);
        return ResponseEntity.ok(readingGoal);
    }

    @Operation(summary = "독서 목표 삭제", description = "독서 목표를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "독서 목표를 찾을 수 없음")
    })
    @DeleteMapping("/{readingGoalId}")
    public void delete(
            @Parameter(description = "독서 목표 ID") @PathVariable Long readingGoalId,
            @AuthenticationPrincipal Long userId
    ) {
        service.delete(readingGoalId, userId);
    }
}
