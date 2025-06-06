package org.nessrev.scriptorium.book.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nessrev.scriptorium.book.dto.BookInfoDto;
import org.nessrev.scriptorium.book.enums.BookStates;
import org.nessrev.scriptorium.book.interfaces.BookRepository;
import org.nessrev.scriptorium.book.mapper.BookMapper;
import org.nessrev.scriptorium.book.models.Book;
import org.nessrev.scriptorium.chapter.interfaces.ChapterRepository;
import org.nessrev.scriptorium.chapter.models.Chapter;
import org.nessrev.scriptorium.image.dto.ImageInfoDto;
import org.nessrev.scriptorium.image.interfaces.ImageService;
import org.nessrev.scriptorium.image.interfaces.ImagesRepository;
import org.nessrev.scriptorium.user.dto.UserInfoDto;
import org.nessrev.scriptorium.user.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final UserService userService;
    private final ImageService imageService;
    private final ImagesRepository imagesRepository;
    private final ChapterRepository chapterRepository;

    public Boolean createBook(Book book, @RequestParam("cover") MultipartFile coverFile, Principal principal){
        ImageInfoDto coverInfo = imageService.save(coverFile);

        book.setCoverId(coverInfo.getId());
        book.setBookStates(Collections.singleton(BookStates.BOOK_PUBLISHED));

        UserInfoDto authorInfo = userService.saveAuthor(principal);
        book.setAuthorId(authorInfo.getId());

        bookRepository.save(book);
        return true;
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getAllBooksById(Long id){
        return bookRepository.findAllByAuthorId(id);
    }

    public boolean deleteBookById(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        List<Chapter> chapters = chapterRepository.findAllByBookId(id);
        if (book == null) return false;
        imagesRepository.deleteById(book.getCoverId());
        chapterRepository.deleteAllInBatch(chapters);
        bookRepository.deleteById(id);
        return true;
    }

    public BookInfoDto saveBook (Book book){
        return BookMapper.toBookDto(book);
    }
}
