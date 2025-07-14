package com.capgemini.twilight.assessment.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Request object for creating or updating a book")
public class BookRequest {

    @Schema(description = "The unique identifier of the author of the book.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long authorId;

    @Schema(description = "The title of the book.", example = "The Hobbit", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "The total number of pages in the book.", example = "310", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pages;

    @Schema(description = "The publication date of the book in YYYY-MM-DD format.", example = "1937-09-21", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate publicationDate;
}