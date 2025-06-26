package org.nessrev.scriptorium.book.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nessrev.scriptorium.book.dto.BookInfoDto;
import org.nessrev.scriptorium.book.mapper.BookMapper;
import org.nessrev.scriptorium.book.model.Book;
import org.nessrev.scriptorium.book.repo.BookRepository;
import org.nessrev.scriptorium.chapter.model.Chapter;
import org.nessrev.scriptorium.chapter.repo.ChapterRepository;
import org.nessrev.scriptorium.elasticSearch.service.BookSearchService;
import org.nessrev.scriptorium.image.dto.ImageInfoDto;
import org.nessrev.scriptorium.image.service.ImageService;
import org.nessrev.scriptorium.image.repo.ImagesRepository;
import org.nessrev.scriptorium.user.dto.UserInfoDto;
import org.nessrev.scriptorium.user.service.UserService;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserService userService;
    @Mock
    private ImageService imageService;
    @Mock
    private ImagesRepository imagesRepository;
    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private BookSearchService bookSearchService;

    @InjectMocks
    private BookService bookService;

    // Arrange (подготовка)
    // Act (действие)
    // Assert (проверка)

    static Stream<Arguments> nullCasesForCreateBook() {
        MockMultipartFile validCoverFile = new MockMultipartFile(
                "cover",
                "cover.jpg",
                "image/jpeg",
                "fake image".getBytes());

        Principal validPrincipal = () -> "mockUsername";

        return Stream.of(
                Arguments.of(null, null, "Both cover and author are null"),
                Arguments.of(null, validPrincipal, "Cover file is null"),
                Arguments.of(validCoverFile, null, "Author is null")
        );
    }

    static Stream<Long> invalidValuesId() {
        return Stream.of(1L, null);
    }

    static Stream<Arguments> nullCasesForSaveBook() {
        return Stream.of(
                Arguments.of(null, null, null),
                Arguments.of(123L, null, null),
                Arguments.of(null, "Twilight", null),
                Arguments.of(null, null, 456L),
                Arguments.of(123L, "Twilight", null),
                Arguments.of(123L, null, 456L),
                Arguments.of(null, "Twilight", 456L)
        );
    }

    static Stream<Arguments> nullCasesForEditBook() {
        Book baseBook = new Book();
        baseBook.setId(123L);
        baseBook.setCoverId(456L);

        MockMultipartFile validCover = new MockMultipartFile(
                "cover", "cover.jpg", "image/jpeg", "fake image".getBytes());
        MockMultipartFile emptyCover = new MockMultipartFile(
                "cover", "empty.jpg", "image/jpeg", new byte[0]);

        return Stream.of(
                Arguments.of(null, "Twilight", "desc", validCover, true, false),
                Arguments.of(baseBook, null, "desc", validCover, true, false),
                Arguments.of(baseBook, " ", "desc", validCover, true, false)
        );
    }

    @Test
    public void createBookTest_success() {
        // arrange
        Book book = new Book();
        MockMultipartFile coverFile = new MockMultipartFile(
                "cover",
                "cover.jpg",
                "image/jpeg",
                "fake image".getBytes());

        ImageInfoDto imageInfo = new ImageInfoDto(123L, "image/jpeg", "fake image");
        UserInfoDto userInfo = new UserInfoDto(456L, "John", "Smith", 789L);

        when(imageService.save(any(MultipartFile.class))).thenReturn(imageInfo);
        when(userService.saveAuthor(any(Principal.class))).thenReturn(userInfo);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        //act
        boolean result = bookService.createBook(book, coverFile, () -> "mockUsername");

        //assert
        assertTrue(result);
        assertEquals(123L, book.getCoverId());
        assertEquals(456L, book.getAuthorId());

        verify(bookRepository).save(book);
        verify(bookSearchService).indexBook(book);
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("nullCasesForCreateBook")
    public void createBookTest_failure(MultipartFile coverFile, Principal principal, String description) {
        Book book = new Book();

        if (principal != null) {
            UserInfoDto userInfo = new UserInfoDto(123L, "John", "Smith", 789L);
            when(userService.saveAuthor(principal)).thenReturn(userInfo);
        } else {
            when(userService.saveAuthor(null)).thenReturn(null);
        }

        if (coverFile != null) {
            ImageInfoDto imageInfo = new ImageInfoDto(123L, "image/jpeg", "fake image");
            when(imageService.save(coverFile)).thenReturn(imageInfo);
        } else {
            when(imageService.save(null)).thenReturn(null);
        }

        boolean result = bookService.createBook(book, coverFile, principal);

        assertFalse(result, "Expected createBook to fail when: " + description);
    }

    @Test
    public void getBookByIdTest_success() {
        Long id = 123L;
        Book book = new Book();
        book.setId(id);

        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @ParameterizedTest
    @MethodSource("invalidValuesId")
    public void getBookById_failure(Long id) {
        when(bookRepository.findById(nullable(Long.class))).thenReturn(Optional.empty());

        Book result = bookService.getBookById(id);

        assertNull(result, "Book search error");
    }

    @Test
    public void getAllBooksById_success() {
        Long id = 123L;
        List<Book> books = new ArrayList<>();
        books.add(new Book());
        books.get(0).setAuthorId(id);

        when(bookRepository.findAllByAuthorId(any(Long.class))).thenReturn(books);

        List<Book> result = bookService.getAllBooksById(id);

        assertNotNull(result);
        assertEquals(books.size(), result.size());
        assertEquals(books.get(0), result.get(0));
    }

    @ParameterizedTest
    @MethodSource("invalidValuesId")
    public void getAllBooksById_failure(Long idTest) {
        when(bookRepository.findAllByAuthorId(nullable(Long.class))).thenReturn(null);

        List<Book> result = bookService.getAllBooksById(idTest);

        assertNull(result, "All books search error");
    }

    @Test
    public void deleteBook_success() {
        Long id = 123L;
        Book book = new Book();
        book.setId(id);
        book.setCoverId(789L);
        List<Chapter> chapters = new ArrayList<>();

        when(chapterRepository.findAllByBookId(any(Long.class))).thenReturn(chapters);
        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.of(book));

        boolean result = bookService.deleteBookById(id);

        assertTrue(result);

        verify(imagesRepository).deleteById(book.getCoverId());
        verify(chapterRepository).deleteAllInBatch(chapters);
        verify(bookRepository).deleteById(id);
        verify(bookSearchService).delete(book);
    }

    @Test
    public void deleteBook_failure_BookNotFound() {
        Long idTest = 1L;
        when(bookRepository.findById(idTest)).thenReturn(Optional.empty());

        boolean result = bookService.deleteBookById(idTest);

        assertFalse(result);
    }

    @Test
    public void deleteBook_failure_IdIsNull() {
        boolean result = bookService.deleteBookById(null);
        assertFalse(result);
    }

    @Test
    public void saveBook_success() {
        Book book = new Book();
        book.setId(123L);
        book.setName("Twilight");
        book.setAuthorId(456L);
        BookInfoDto bookInfo = new BookInfoDto(123L, "Twilight", 456L);

        try (MockedStatic<BookMapper> mocked = Mockito.mockStatic(BookMapper.class)) {
            mocked.when(() -> BookMapper.toBookDto(any(Book.class))).thenReturn(bookInfo);

            BookInfoDto result = bookService.saveBook(book);

            assertNotNull(result);
            assertEquals(bookInfo.getId(), result.getId());
            assertEquals(bookInfo.getName(), result.getName());
            assertEquals(bookInfo.getAuthorId(), result.getAuthorId());
        }
    }

    @ParameterizedTest
    @MethodSource("nullCasesForSaveBook")
    public void saveBook_failure(Long idBook, String bookName, Long authorId) {
        Book book = new Book();
        book.setId(idBook);
        book.setName(bookName);
        book.setAuthorId(authorId);

        try (MockedStatic<BookMapper> mocked = Mockito.mockStatic(BookMapper.class)) {
            mocked.when(() -> BookMapper.toBookDto(book)).thenReturn(null);

            BookInfoDto result = bookService.saveBook(book);

            assertNull(result);
        }
    }

    @Test
    public void editBook_success() {
        Book book = new Book();
        book.setId(123L);
        book.setCoverId(456L);
        String name = "Twilight";
        String description = "book about vampire";
        MockMultipartFile coverFile = new MockMultipartFile(
                "cover",
                "cover.jpg",
                "image/jpeg",
                "fake image".getBytes());
        boolean isPublic = true;

        Long oldId = book.getCoverId();

        ImageInfoDto coverFileInfo = new ImageInfoDto(786L, "image/jpeg", "fake image");

        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(imageService.save(any(MultipartFile.class))).thenReturn(coverFileInfo);

        boolean result = bookService.editBook(book, name, description, coverFile, isPublic);

        assertTrue(result);
        assertEquals(123L, book.getId());
        assertEquals("Twilight", book.getName());
        assertEquals("book about vampire", book.getDescription());
        assertEquals(786L, book.getCoverId());
        assertEquals(true, book.getIsPublic());

        verify(imagesRepository).deleteById(oldId);
        verify(bookRepository).save(book);
        verify(bookSearchService).indexBook(book);

    }


    @ParameterizedTest
    @MethodSource("nullCasesForEditBook")
    public void editBook_failure(Book book,
                                 String name,
                                 String description,
                                 MultipartFile coverFile,
                                 Boolean isPublic,
                                 boolean expectSaveImage) {

        if (expectSaveImage) {
            when(imageService.save(any(MultipartFile.class)))
                    .thenReturn(new ImageInfoDto(999L, "image/jpeg", "mocked image"));
        }

        boolean result = bookService.editBook(book, name, description, coverFile, isPublic);

        assertFalse(result);

        verify(bookRepository, never()).save(any());
        verify(bookSearchService, never()).indexBook(any());
        verify(imageService, never()).save(isNull());
        verify(imagesRepository, never()).deleteById(any());
    }

    @Test
    public void getAllPublicBooks_success(){
        List<Book> publicBooks = new ArrayList<>();
        publicBooks.add(new Book());
        publicBooks.add(new Book());
        publicBooks.get(0).setIsPublic(true);
        publicBooks.get(1).setIsPublic(true);

        when(bookRepository.findAllPublicBooks()).thenReturn(publicBooks);

        List<Book> result = bookService.getAllPublicBooks();

        assertNotNull(result);
        assertEquals(publicBooks.size(), result.size());
        assertEquals(publicBooks.get(0), result.get(0));
        assertEquals(publicBooks.get(1), result.get(1));
    }

    @Test
    public void deleteBooksByUserId_success(){
        Long userId = 123L;
        List<Book> books = new ArrayList<>();
        books.add(new Book());
        books.add(new Book());
        books.get(0).setAuthorId(userId);
        books.get(1).setAuthorId(userId);
        books.get(0).setId(123L);
        books.get(1).setId(456L);

        when(bookRepository.findAllByAuthorId(userId)).thenReturn(books);

        BookService spyService = Mockito.spy(bookService);
        doReturn(true).when(spyService).deleteBookById(anyLong());

        boolean result = spyService.deleteBooksByUserId(userId);

        assertTrue(result);
        verify(bookRepository).findAllByAuthorId(userId);
        verify(spyService).deleteBookById(123L);
        verify(spyService).deleteBookById(456L);
    }

    @ParameterizedTest
    @MethodSource("invalidValuesId")
    public void deleteBookByUserId_failure(Long wrongUserId) {

        when(bookRepository.findAllByAuthorId(nullable(Long.class))).thenReturn(Collections.emptyList());

        boolean result = bookService.deleteBooksByUserId(wrongUserId);

        assertFalse(result);
        verify(bookRepository).findAllByAuthorId(nullable(Long.class));
    }
}
