package org.nessrev.scriptorium.controller;

import lombok.AllArgsConstructor;
import org.nessrev.scriptorium.book.models.Book;
import org.nessrev.scriptorium.book.services.BookService;
import org.nessrev.scriptorium.user.dto.UserInfoDto;
import org.nessrev.scriptorium.user.mapper.UserMapper;
import org.nessrev.scriptorium.user.models.User;
import org.nessrev.scriptorium.user.services.UserHelperService;
import org.nessrev.scriptorium.user.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class MainController {
    private final UserHelperService userHelperService;
    private final BookService bookService;
    private final UserService userService;

    @GetMapping("/main")
    public String mainPage(Principal principal, Model model) throws AccessDeniedException {
        User user = userHelperService.getUserByPrincipal(principal);

        List<Book> books = bookService.getAllPublicBooks();
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
        return "main";
    }
}
