package com.capgemini.twilight.assessment.book.service.search;

import com.capgemini.twilight.assessment.book.repository.BookRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class LastModifiedBookSearchStrategy implements BookSearchStrategy {
    @Override
    public ResponseEntity<?> search(BookRepository repository, LocalDate fromDate, LocalDate toDate) {
        return repository.findTopByOrderByLastModifiedDateDesc()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public String getQueryName() {
        return "last-modified";
    }
}