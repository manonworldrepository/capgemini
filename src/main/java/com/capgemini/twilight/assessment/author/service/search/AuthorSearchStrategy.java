package com.capgemini.twilight.assessment.author.service.search;

import com.capgemini.twilight.assessment.author.model.Author;
import com.capgemini.twilight.assessment.author.repository.AuthorRepository;
import java.util.Optional;

public interface AuthorSearchStrategy {
    Optional<Author> search(AuthorRepository repository);
    String getQueryName();
}
