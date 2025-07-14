package com.capgemini.twilight.assessment.book.service.search;

import com.capgemini.twilight.assessment.book.repository.BookRepository;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class LongestBookSearchStrategy implements BookSearchStrategy {

    @Override
    public Object search(BookRepository repository, LocalDate fromDate, LocalDate toDate) {
        return repository.findTopByOrderByPagesDesc();
    }

    @Override
    public String getQueryName() {
        return "longest";
    }
}