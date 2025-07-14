package com.capgemini.twilight.assessment.book.model;

import com.capgemini.twilight.assessment.author.model.Author;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "book", indexes = {
    @Index(
        name = "idx_book_publication_date",
        columnList = "publicationDate"
    ),
    @Index(
        name = "idx_book_pages",
        columnList = "pages"
    ),
    @Index(
        name = "idx_book_last_modified_date",
        columnList = "lastModifiedDate"
    )
})
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Integer pages;

    private LocalDate publicationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonBackReference
    private Author author;

    @CreationTimestamp
    private Instant createdDate;

    @UpdateTimestamp
    private Instant lastModifiedDate;
}