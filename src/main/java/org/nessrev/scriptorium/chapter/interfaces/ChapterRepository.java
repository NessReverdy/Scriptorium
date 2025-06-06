package org.nessrev.scriptorium.chapter.interfaces;

import org.nessrev.scriptorium.book.models.Book;
import org.nessrev.scriptorium.chapter.models.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    @Transactional
    List<Chapter> findAllByBookId(Long id);
}
