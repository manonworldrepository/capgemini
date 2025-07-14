package com.capgemini.twilight.assessment.author.repository;

import com.capgemini.twilight.assessment.author.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query(value = "SELECT a.* FROM author a LEFT JOIN book b ON a.id = b.author_id GROUP BY a.id ORDER BY COUNT(b.id) DESC LIMIT 1", nativeQuery = true)
    Optional<Author> findAuthorWithMostBooks();

    Optional<Author> findTopByOrderByLastModifiedDateDesc();

}