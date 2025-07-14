package com.capgemini.twilight.assessment.author.service.search;

import com.capgemini.twilight.assessment.author.model.Author;
import com.capgemini.twilight.assessment.author.repository.AuthorRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LastModifiedAuthorSearchStrategy implements AuthorSearchStrategy {

    @Override
    public Optional<Author> search(AuthorRepository repository) {
        return repository.findTopByOrderByLastModifiedDateDesc();
    }

    @Override
    public String getQueryName() {
        return "last-modified";
    }
}