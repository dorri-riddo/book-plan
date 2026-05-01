package com.example.bookplan.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "책 수정 요청")
public class BookUpdateRequest {
    @Schema(description = "책 제목", example = "클린 코드")
    @Size(max = 500)
    private String title;

    @Schema(description = "총 페이지 수", example = "464")
    @PositiveOrZero
    private Integer totalPages;

    @Schema(description = "저자", example = "로버트 C. 마틴")
    @Size(max = 200)
    private String authors;

    @Schema(description = "역자", example = "박재호")
    @Size(max = 200)
    private String translators;

    @Schema(description = "출판사", example = "인사이트")
    @Size(max = 500)
    private String publisher;

    @Schema(description = "ISBN (13자리)", example = "9788966260959")
    @Size(min = 13, max = 13)
    private String isbn;

    @Schema(description = "표지 이미지 URL", example = "https://example.com/cover.jpg")
    @Size(max = 500)
    private String coverImageUrl;
}
