package org.nessrev.scriptorium.chapter.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nessrev.scriptorium.book.dto.BookInfoDto;
import org.nessrev.scriptorium.book.model.Book;
import org.nessrev.scriptorium.book.service.BookService;
import org.nessrev.scriptorium.chapter.repo.ChapterRepository;
import org.nessrev.scriptorium.chapter.model.Chapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ChapterService {
    private final BookService bookService;
    private final ChapterRepository chapterRepository;

    @Transactional
    public Chapter createChapter(Chapter chapter, Book book){
        chapter.setId(null);

        BookInfoDto bookInfo = bookService.saveBook(book);

        chapter.setBookId(bookInfo.getId());

        return chapterRepository.save(chapter);
    }

    public Chapter getChapterById(Long id) {
        return chapterRepository.findById(id).orElse(null);
    }

    public List<Chapter> getListOfChaptersByBookId(Long id) {
        return chapterRepository.findAllByBookId(id);
    }

    public boolean deleteChapterById(Long id) {
        chapterRepository.deleteById(id);
        return true;
    }

    public boolean editChapter(Chapter chapter,
                               String title,
                               String description,
                               boolean isPublicChapter) {
        if (title != null && !title.trim().isEmpty()) {
            chapter.setTitle(title);
        }
        if (description != null && !description.trim().isEmpty()) {
            chapter.setDescription(description);
        }

        chapter.setIsPublicChapter(isPublicChapter);
        chapterRepository.save(chapter);
        log.info("Chapter edited successfully");
        return true;
    }
}
