package com.example.bookplan.book;

import com.example.bookplan.book.dto.BookCreateRequest;
import com.example.bookplan.book.dto.BookUpdateRequest;
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

@Tag(name = "Book", description = "책 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("books")
public class BookController {
    private final BookService service;

    // 1. Book 에 대해서 연재 소설이나 이북도 넣을 수 있도록 n 권 / n 화 처럼..
    // unit 컬럼 추가 예정 totalPage 대신 totalCount 로 바꾸고
    // 2. list 조회 시 페이지네이션 및 검색 조건 추가 + 정렬 조건

    @Operation(summary = "책 목록 조회", description = "사용자가 등록한 모든 책을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping
    public ResponseEntity<List<Book>> findAll(
            @AuthenticationPrincipal Long userId
    ) {
        List<Book> books = service.findAll(userId);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "책 단건 조회", description = "책 ID로 특정 책을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "책을 찾을 수 없음")
    })
    @GetMapping("/{bookId}")
    public ResponseEntity<Book> findOne(
            @Parameter(description = "책 ID") @PathVariable Long bookId,
            @AuthenticationPrincipal Long userId) {
        Book book = service.findOne(bookId, userId);
        return ResponseEntity.ok(book);
    }

    @Operation(summary = "책 등록", description = "사용자가 읽을 책을 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "등록 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    public ResponseEntity<Book> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody BookCreateRequest request) {
        Book book = service.create(request, userId);
        return ResponseEntity.ok(book);
    }

    @Operation(summary = "책 수정", description = "등록된 책 정보를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "책을 찾을 수 없음")
    })
    @PatchMapping("/{bookId}")
    public ResponseEntity<Book> update(
            @Parameter(description = "책 ID") @PathVariable Long bookId,
            @AuthenticationPrincipal long userId,
            @Valid @RequestBody BookUpdateRequest request) {
        Book book = service.update(request, bookId, userId);
        return ResponseEntity.ok(book);
    }

    @Operation(summary = "책 삭제", description = "등록된 책을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "책을 찾을 수 없음")
    })
    @DeleteMapping("/{bookId}")
    public void delete(
            @Parameter(description = "책 ID") @PathVariable Long bookId,
            @AuthenticationPrincipal long userId) {
        service.delete(bookId, userId);
    }
}
