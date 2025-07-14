package com.capgemini.twilight.assessment.book.repository;

import com.capgemini.twilight.assessment.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findTopByOrderByPagesDesc();

    Optional<Book> findTopByOrderByPublicationDateAsc();

    Optional<Book> findTopByOrderByLastModifiedDateDesc();

    List<Book> findByPublicationDateBetween(LocalDate fromDate, LocalDate toDate);

    List<Book> findByPublicationDateGreaterThanEqual(LocalDate date);

    List<Book> findByPublicationDateLessThanEqual(LocalDate date);
}