package org.nessrev.scriptorium.chapter.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nessrev.scriptorium.book.models.Book;
import org.nessrev.scriptorium.book.services.BookService;
import org.nessrev.scriptorium.chapter.models.Chapter;
import org.nessrev.scriptorium.chapter.services.ChapterService;
import org.nessrev.scriptorium.user.models.User;
import org.nessrev.scriptorium.user.services.UserHelperService;
import org.nessrev.scriptorium.user.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChapterController {
    private final ChapterService chapterService;
    private final BookService bookService;
    private final UserHelperService userHelperService;
    private final UserService userService;

    @GetMapping("/createChapter/{id}")
    public String createChapter(@PathVariable Long id, Model model, Principal principal) throws AccessDeniedException {
        User user = userHelperService.getUserByPrincipal(principal);
        Book book = bookService.getBookById(id);
        model.addAttribute("user", user);
        model.addAttribute("book", book);
        model.addAttribute("chapter", new Chapter());
        return "createChapter";
    }

    @PostMapping("/createChapter/{id}")
    public String endOfCreatingChapter(@PathVariable Long id,
                                       @ModelAttribute("chapter") @Valid Chapter chapter,
                                       RedirectAttributes redirectAttributes){

        Book book = bookService.getBookById(id);
        Chapter savedChapter = chapterService.createChapter(chapter, book);
        if (savedChapter == null) {
            redirectAttributes.addFlashAttribute("error", "❌ Error creating chapter");
            return "redirect:/createChapter?error";
        }

        return "redirect:/chapter/" + savedChapter.getId();
    }

    @GetMapping("/chapter/{id}")
    public String chapter(@PathVariable Long id, Model model, Principal principal) throws AccessDeniedException {
        User user = userHelperService.getUserByPrincipal(principal);
        Chapter chapter = chapterService.getChapterById(id);
        Book book = bookService.getBookById(chapter.getBookId());

        User author = userService.getUserById(book.getAuthorId());
        boolean isOwner = user != null && user.getId().equals(author.getId());
        List<Chapter> chapters = chapterService.getListOfChaptersByBookId(book.getId());
        chapters.sort(Comparator.comparing(Chapter::getId));

        List<Chapter> visibleChapters = isOwner
                ? chapters
                : chapters.stream()
                .filter(Chapter::getIsPublicChapter)
                .toList();
        model.addAttribute("visibleChapters", visibleChapters);
        model.addAttribute("user", user);
        model.addAttribute("book", book);
        model.addAttribute("chapter", chapter);
        return "chapter";
    }

    @PostMapping("/chapter/delete/{id}")
    public String deleteChapter(@PathVariable Long id) {
        Chapter chapter = chapterService.getChapterById(id);
        Long bookId = bookService.getBookById(chapter.getBookId()).getId();
        if (!chapterService.deleteChapterById(id)) {
            log.error("Error in deleting a chapter");
        }
        return "redirect:/book/" + bookId;
    }

    @GetMapping("/editChapter/{id}")
    public String editChapter(@PathVariable Long id, Model model, Principal principal) throws AccessDeniedException {
        Chapter chapter = chapterService.getChapterById(id);
        Book book = bookService.getBookById(chapter.getBookId());

        model.addAttribute("book", book);
        model.addAttribute("chapter", chapter);
        model.addAttribute("user", userHelperService.getUserByPrincipal(principal));
        return "editChapter";
    }

    @PostMapping("/editChapter/{id}")
    public String editChapter(@PathVariable Long id,
                              @RequestParam(required = false) String title,
                              @RequestParam(required = false) String description,
                              @RequestParam("isPublicChapter") boolean isPublicChapter) {
        Chapter existingChapter = chapterService.getChapterById(id);

        if (!chapterService.editChapter(existingChapter, title, description, isPublicChapter)){
            log.error("Couldn't to edit the chapter");
        }
        return "redirect:/book/" + existingChapter.getBookId();
    }
}
