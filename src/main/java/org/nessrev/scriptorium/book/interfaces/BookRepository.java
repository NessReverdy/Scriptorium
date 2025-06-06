package org.nessrev.scriptorium.book.interfaces;

import org.nessrev.scriptorium.book.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Transactional
    List<Book> findAllByAuthorId(Long id);
}
