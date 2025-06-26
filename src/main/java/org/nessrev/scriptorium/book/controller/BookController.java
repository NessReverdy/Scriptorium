package org.nessrev.scriptorium.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nessrev.scriptorium.book.model.Book;
import org.nessrev.scriptorium.book.service.BookService;
import org.nessrev.scriptorium.chapter.model.Chapter;
import org.nessrev.scriptorium.chapter.service.ChapterService;
import org.nessrev.scriptorium.user.model.User;
import org.nessrev.scriptorium.user.service.UserHelperService;
import org.nessrev.scriptorium.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BookController {
    private final BookService bookService;
    private final UserHelperService userHelperService;
    private final UserService userService;
    private final ChapterService chapterService;

    @GetMapping("/createBook")
    public String createBook(Model model, Principal principal) throws AccessDeniedException {
        User user = userHelperService.getUserByPrincipal(principal);
        model.addAttribute("user", user);
        model.addAttribute("book", new Book());
        return "createBook";
    }


    @PostMapping("/createBook")
    public String endOfCreateBook(@ModelAttribute("book") @Valid Book book,
                                  BindingResult bindingResult,
                                  Model model,
                                  @RequestParam("cover") MultipartFile multipartFile,
                                  Principal principal) throws AccessDeniedException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userHelperService.getUserByPrincipal(principal));
            return "createBook";
        }

        if (!bookService.createBook(book, multipartFile, principal)) {
            model.addAttribute("error", "Could not create book");
            return "createBook";
        }

        Long createdBookId = book.getId();
        return "redirect:/book/" + createdBookId;
    }


    @GetMapping("/book/{id}")
    public String viewBookInfo(@PathVariable Long id,
                               Model model,
                               Principal principal) throws AccessDeniedException {
        Book book = bookService.getBookById(id);
        User author = userService.getUserById(book.getAuthorId());
        User currentUser = userHelperService.getUserByPrincipal(principal);
        boolean isOwner = currentUser != null && currentUser.getId().equals(author.getId());

        List<Chapter> chapters = chapterService.getListOfChaptersByBookId(book.getId());
        chapters.sort(Comparator.comparing(Chapter::getId));

        model.addAttribute("book", book);
        model.addAttribute("user", currentUser);
        model.addAttribute("author", author);
        model.addAttribute("cover", book.getCoverId() != null
                ? "/api/images/" + book.getCoverId()
                : "/images/defaultImg.jpg");
        model.addAttribute("avatar", author.getAvatarId() != null
                ? "/api/images/" + author.getAvatarId()
                : "/images/defaultImg.jpg");
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("chapters", chapters);

        List<Chapter> visibleChapters = isOwner
                ? chapters
                : chapters.stream()
                .filter(Chapter::getIsPublicChapter)
                .toList();
        model.addAttribute("visibleChapters", visibleChapters);

        return "book";
    }


    @PostMapping("/book/delete/{id}")
    public String deleteBook(@PathVariable Long id){
        if (!bookService.deleteBookById(id)){
            log.error("Could not delete book");
        }
        return "redirect:/my-profile";
    }

    @PostMapping("/book/edit/{id}")
    public String editBook(@PathVariable Long id,
                                     @RequestParam(required = false) String name,
                                     @RequestParam(required = false) String description,
                                     @RequestParam(required = false) MultipartFile coverFile,
                                     @RequestParam("isPublic") boolean isPublic) {
        Book existingBook = bookService.getBookById(id);
        if (!bookService.editBook(existingBook,name, description, coverFile, isPublic)){
            log.error("Couldn't to edit the book");
        }
        return "redirect:/book/" + id;
    }
}
