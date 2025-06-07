package org.nessrev.scriptorium.book.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nessrev.scriptorium.book.dto.BookInfoDto;
import org.nessrev.scriptorium.book.repo.BookRepository;
import org.nessrev.scriptorium.book.mapper.BookMapper;
import org.nessrev.scriptorium.book.models.Book;
import org.nessrev.scriptorium.chapter.repo.ChapterRepository;
import org.nessrev.scriptorium.chapter.models.Chapter;
import org.nessrev.scriptorium.image.dto.ImageInfoDto;
import org.nessrev.scriptorium.image.interfaces.ImageService;
import org.nessrev.scriptorium.image.interfaces.ImagesRepository;
import org.nessrev.scriptorium.user.dto.UserInfoDto;
import org.nessrev.scriptorium.user.services.UserService;
import org.springframework.stereotype.Service;
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

    public Boolean createBook(Book book, @RequestParam("cover") MultipartFile coverFile, Principal principal){
        ImageInfoDto coverInfo = imageService.save(coverFile);

        book.setCoverId(coverInfo.getId());

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

    public boolean editBook(Book book,
                            String name,
                            String description,
                            @RequestParam("cover") MultipartFile coverFile,
                            boolean isPublic){
        if (name != null && !name.trim().isEmpty()) {
            book.setName(name);
        }
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
        return true;
    }

    public List<Book> getAllPublicBooks(){
        return bookRepository.findAllPublicBooks();
    }
}
