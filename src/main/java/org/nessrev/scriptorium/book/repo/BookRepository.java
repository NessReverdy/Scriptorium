package org.nessrev.scriptorium.book.repo;

import org.nessrev.scriptorium.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Transactional
    List<Book> findAllByAuthorId(Long id);

    @Transactional
    @Query("SELECT book FROM Book book WHERE book.isPublic = true")
    List<Book> findAllPublicBooks();
}
