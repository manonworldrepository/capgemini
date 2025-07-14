package com.capgemini.twilight.assessment.book.controller;

import com.capgemini.twilight.assessment.author.model.Author;
import com.capgemini.twilight.assessment.author.repository.AuthorRepository;
import com.capgemini.twilight.assessment.book.dto.BookRequest;
import com.capgemini.twilight.assessment.book.model.Book;
import com.capgemini.twilight.assessment.book.repository.BookRepository;
import com.capgemini.twilight.assessment.book.service.search.BookSearchStrategy;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Tag(name = "Book Management", description = "Endpoints for creating, retrieving, and searching books")
@RestController
@RequestMapping("/book")
public class BookController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final List<BookSearchStrategy> strategyList;
    private Map<String, BookSearchStrategy> searchStrategies;

    public BookController(BookRepository bookRepository, AuthorRepository authorRepository, List<BookSearchStrategy> strategies) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.strategyList = strategies;
    }

    @PostConstruct
    private void initStrategies() {
        this.searchStrategies = strategyList.stream()
            .collect(Collectors.toMap(BookSearchStrategy::getQueryName, Function.identity()));
    }

    @Operation(summary = "Create a new book")
    @ApiResponse(
        responseCode = "201",
        description = "Book created successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Book.class)
        )
    )
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody BookRequest request) {
        Author author = authorRepository.findById(request.getAuthorId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cannot create book for non-existent author with id: " + request.getAuthorId()
            ));

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setPages(request.getPages());
        book.setPublicationDate(request.getPublicationDate());
        book.setAuthor(author);
        Book savedBook = bookRepository.save(book);
        author.getBooks().add(savedBook);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @Operation(summary = "Get a book by ID")
    @ApiResponse(
        responseCode = "200",
        description = "Book found",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Book.class)
        )
    )
    @GetMapping("/{id}")
    public Book getBookById(@Parameter(description = "ID of the book to retrieve", required = true) @PathVariable Long id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    @Operation(summary = "Get all books")
    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Operation(summary = "Update an existing book")
    @ApiResponse(
        responseCode = "200",
        description = "Book updated successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Book.class)
        )
    )
    @PutMapping("/{id}")
    public Book updateBook(
        @Parameter(description = "ID of the book to update", required = true) @PathVariable Long id,
        @RequestBody BookRequest request
    ) {
        Book existingBook = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        existingBook.setTitle(request.getTitle());
        existingBook.setPages(request.getPages());
        existingBook.setPublicationDate(request.getPublicationDate());
        return bookRepository.save(existingBook);
    }

    @Operation(summary = "Search for books")
    @GetMapping("/search")
    public Object searchBooks(
        @Parameter(
            description = "The search query type. Supported values: 'longest', 'oldest', 'last-modified', 'by-date'.",
            required = true,
            example = "longest"
        )
        @RequestParam String query,
        @Parameter(description = "The start date for a 'by-date' search (inclusive). Format: YYYY-MM-DD.")
        @RequestParam(required = false) LocalDate fromDate,
        @Parameter(description = "The end date for a 'by-date' search (inclusive). Format: YYYY-MM-DD.")
        @RequestParam(required = false) LocalDate toDate
    ) {
        BookSearchStrategy strategy = searchStrategies.get(query);
        if (strategy == null) {
            throw new InvalidRequestException("Invalid query parameter: " + query);
        }

        Object result = strategy.search(bookRepository, fromDate, toDate);

        if (result instanceof Optional) {
            @SuppressWarnings("unchecked")
            Optional<Book> optionalBook = (Optional<Book>) result;
            return optionalBook.orElseThrow(() -> new ResourceNotFoundException("Book not found for query: " + query));
        }

        return result;
    }
}