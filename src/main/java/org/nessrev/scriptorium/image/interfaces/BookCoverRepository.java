package org.nessrev.scriptorium.image.interfaces;

import org.nessrev.scriptorium.image.models.BookCover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCoverRepository extends JpaRepository<BookCover, Long> {
}
