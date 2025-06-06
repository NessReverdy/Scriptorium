package org.nessrev.scriptorium.chapter.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nessrev.scriptorium.book.dto.BookInfoDto;
import org.nessrev.scriptorium.book.models.Book;
import org.nessrev.scriptorium.book.services.BookService;
import org.nessrev.scriptorium.chapter.enums.ChapterStates;
import org.nessrev.scriptorium.chapter.interfaces.ChapterRepository;
import org.nessrev.scriptorium.chapter.models.Chapter;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ChapterService {
    private final BookService bookService;
    private final ChapterRepository chapterRepository;


    public Chapter createChapter(Chapter chapter, Book book){
        chapter.setId(null);

        chapter.setChapterStates(Collections.singleton(ChapterStates.CHAPTER_PUBLISHED));
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
}
