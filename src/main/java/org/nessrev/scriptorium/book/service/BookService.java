package org.nessrev.scriptorium.book.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nessrev.scriptorium.book.dto.BookInfoDto;
import org.nessrev.scriptorium.book.repo.BookRepository;
import org.nessrev.scriptorium.book.mapper.BookMapper;
import org.nessrev.scriptorium.book.model.Book;
import org.nessrev.scriptorium.chapter.repo.ChapterRepository;
import org.nessrev.scriptorium.chapter.model.Chapter;
import org.nessrev.scriptorium.elasticSearch.service.BookSearchService;
import org.nessrev.scriptorium.image.dto.ImageInfoDto;
import org.nessrev.scriptorium.image.service.ImageService;
import org.nessrev.scriptorium.image.repo.ImagesRepository;
import org.nessrev.scriptorium.user.dto.UserInfoDto;
import org.nessrev.scriptorium.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
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
    private final BookSearchService bookSearchService;

    @Transactional
    public Boolean createBook(Book book, @RequestParam("cover") MultipartFile coverFile, Principal principal){
        ImageInfoDto coverInfo = imageService.save(coverFile);
        UserInfoDto authorInfo = userService.saveAuthor(principal);

        if (coverInfo == null || authorInfo == null){
            return false;
        }

        book.setCoverId(coverInfo.getId());
        book.setAuthorId(authorInfo.getId());

        bookRepository.save(book);
        bookSearchService.indexBook(book);
        return true;
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public List<Book> getAllBooksById(Long id){
        return bookRepository.findAllByAuthorId(id);
    }

    @Transactional
    public boolean deleteBookById(Long id) {
        if (id == null) return false;
        Book book = bookRepository.findById(id).orElse(null);
        List<Chapter> chapters = chapterRepository.findAllByBookId(id);
        if (book == null) return false;
        imagesRepository.deleteById(book.getCoverId());
        chapterRepository.deleteAllInBatch(chapters);
        bookRepository.deleteById(id);
        bookSearchService.delete(book);
        return true;
    }

    @Transactional
    public BookInfoDto saveBook (Book book){
        return BookMapper.toBookDto(book);
    }

    @Transactional
    public boolean editBook(Book book,
                            String name,
                            String description,
                            @RequestParam("cover") MultipartFile coverFile,
                            boolean isPublic){
        if (book == null) return false;
        if (name == null || name.trim().isEmpty()) return false;

        book.setName(name);

        if (description != null && !description.trim().isEmpty()) {
            book.setDescription(description);
        }
        if (coverFile != null && !coverFile.isEmpty()) {
            imagesRepository.deleteById(book.getCoverId());
            ImageInfoDto coverInfo = imageService.save(coverFile);
            book.setCoverId(coverInfo.getId());
        }
        book.setIsPublic(isPublic);
        bookRepository.save(book);
        bookSearchService.indexBook(book);
        log.info("Book edited successfully");
        return true;
    }

    public List<Book> getAllPublicBooks(){
        return bookRepository.findAllPublicBooks();
    }

    @Transactional
    public boolean deleteBooksByUserId(Long userId) {
        List<Book> books = bookRepository.findAllByAuthorId(userId);
        if (books.isEmpty()) {
            return false;
        }
        books.forEach(book -> deleteBookById(book.getId()));
        return true;
    }
}
