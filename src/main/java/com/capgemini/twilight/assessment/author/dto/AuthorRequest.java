package com.capgemini.twilight.assessment.author.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request object for creating or updating an author")
public class AuthorRequest {

    @Schema(
        description = "The full name of the author.",
        example = "J.R.R. Tolkien",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;
}