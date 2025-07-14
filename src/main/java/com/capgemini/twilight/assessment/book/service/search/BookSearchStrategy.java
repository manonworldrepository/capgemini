package com.capgemini.twilight.assessment.book.service.search;

import com.capgemini.twilight.assessment.book.repository.BookRepository;
import java.time.LocalDate;

public interface BookSearchStrategy {
    Object search(BookRepository repository, LocalDate fromDate, LocalDate toDate);
    String getQueryName();
}