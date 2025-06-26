package org.nessrev.scriptorium.controller;

import lombok.AllArgsConstructor;
import org.nessrev.scriptorium.book.model.Book;
import org.nessrev.scriptorium.book.service.BookService;
import org.nessrev.scriptorium.elasticSearch.model.BookDocument;
import org.nessrev.scriptorium.elasticSearch.service.BookSearchService;
import org.nessrev.scriptorium.user.dto.UserInfoDto;
import org.nessrev.scriptorium.user.mapper.UserMapper;
import org.nessrev.scriptorium.user.model.User;
import org.nessrev.scriptorium.user.service.UserHelperService;
import org.nessrev.scriptorium.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class MainController {
    private final UserHelperService userHelperService;
    private final BookService bookService;
    private final UserService userService;
    private final BookSearchService bookSearchService;

    @GetMapping("/main")
    public String mainPage(@RequestParam(value = "keyword", required = false) String keyword,
                           Principal principal,
                           Model model) throws AccessDeniedException {
        User user = userHelperService.getUserByPrincipal(principal);

        List<Book> books;
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<BookDocument> bookDocs = bookSearchService.search(keyword);
            books = bookDocs.stream()
                    .map(doc -> bookService.getBookById(doc.getId()))
                    .filter(Objects::nonNull)
                    .toList();
        } else {
            books = bookService.getAllPublicBooks();
        }

        Map<Long, UserInfoDto> authorsMap = books.stream()
                .map(Book::getAuthorId)
                .distinct()
                .collect(Collectors.toMap(
                        Function.identity(),
                        id -> UserMapper.toUserDto(userService.getUserById(id))
                ));

        model.addAttribute("authorsMap", authorsMap);
        model.addAttribute("books", books);
        model.addAttribute("user", user);
        model.addAttribute("keyword", keyword);
        return "main";
    }

}
