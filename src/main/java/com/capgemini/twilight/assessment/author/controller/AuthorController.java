package com.capgemini.twilight.assessment.author.controller;

import com.capgemini.twilight.assessment.author.dto.AuthorRequest;
import com.capgemini.twilight.assessment.author.model.Author;
import com.capgemini.twilight.assessment.author.repository.AuthorRepository;
import com.capgemini.twilight.assessment.author.service.search.AuthorSearchStrategy;
import com.capgemini.twilight.assessment.exception.InvalidRequestException;
import com.capgemini.twilight.assessment.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Tag(
    name = "Author Management",
    description = "Endpoints for creating, retrieving, and searching authors"
)
@RestController
@RequestMapping("/author")
public class AuthorController {

    private final AuthorRepository authorRepository;
    private final List<AuthorSearchStrategy> strategyList;
    private Map<String, AuthorSearchStrategy> searchStrategies;

    public AuthorController(AuthorRepository authorRepository, List<AuthorSearchStrategy> strategies) {
        this.authorRepository = authorRepository;
        this.strategyList = strategies;
    }

    @PostConstruct
    private void initStrategies() {
        this.searchStrategies = strategyList.stream()
            .collect(Collectors.toMap(AuthorSearchStrategy::getQueryName, Function.identity()));
    }

    @Operation(summary = "Create a new author")
    @ApiResponse(
        responseCode = "201",
        description = "Author created successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Author.class)
        )
    )
    @PostMapping
    public ResponseEntity<Author> createAuthor(@RequestBody AuthorRequest request) {
        Author author = new Author();
        author.setName(request.getName());
        Author savedAuthor = authorRepository.save(author);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAuthor);
    }

    @Operation(summary = "Get an author by ID")
    @ApiResponse(
        responseCode = "200",
        description = "Author found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Author.class))
    )
    @GetMapping("/{id}")
    public Author getAuthorById(@Parameter(description = "ID of the author to retrieve", required = true) @PathVariable Long id) {
        return authorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
    }

    @Operation(summary = "Get all authors")
    @GetMapping
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @Operation(summary = "Update an existing author")
    @ApiResponse(
        responseCode = "200",
        description = "Author updated successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Author.class)
        )
    )
    @PutMapping("/{id}")
    public Author updateAuthor(
        @Parameter(description = "ID of the author to update", required = true) @PathVariable Long id,
        @RequestBody AuthorRequest request
    ) {
        Author existingAuthor = authorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));

        existingAuthor.setName(request.getName());
        return authorRepository.save(existingAuthor);
    }

    @Operation(summary = "Search for an author")
    @GetMapping("/search")
    public Author searchAuthors(
        @Parameter(
            description = "The search query type. Supported values: 'most-books', 'last-modified'.",
            required = true,
            example = "most-books"
        )
        @RequestParam String query
    ) {
        AuthorSearchStrategy strategy = searchStrategies.get(query);

        if (strategy == null) {
            throw new InvalidRequestException("Invalid query parameter: " + query);
        }

        return strategy.search(authorRepository)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found for query: " + query));
    }
}
