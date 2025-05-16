package org.nessrev.scriptorium.book.interfaces;

import org.nessrev.scriptorium.book.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
