package com.capgemini.twilight.assessment.book.service.search;

import com.capgemini.twilight.assessment.book.repository.BookRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ByDateBookSearchStrategy implements BookSearchStrategy {
    @Override
    public ResponseEntity<?> search(BookRepository repository, LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null) {
            return ResponseEntity.ok(repository.findByPublicationDateBetween(fromDate, toDate));
        } else if (fromDate != null) {
            return ResponseEntity.ok(repository.findByPublicationDateGreaterThanEqual(fromDate));
        } else if (toDate != null) {
            return ResponseEntity.ok(repository.findByPublicationDateLessThanEqual(toDate));
        }
        return ResponseEntity.ok(repository.findAll());
    }

    @Override
    public String getQueryName() {
        return "by-date";
    }
}