package org.nessrev.scriptorium.chapter.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nessrev.scriptorium.book.models.Book;
import org.nessrev.scriptorium.book.services.BookService;
import org.nessrev.scriptorium.chapter.models.Chapter;
import org.nessrev.scriptorium.chapter.services.ChapterService;
import org.nessrev.scriptorium.user.models.User;
import org.nessrev.scriptorium.user.services.UserHelperService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChapterController {
    private final ChapterService chapterService;
    private final BookService bookService;
    private final UserHelperService userHelperService;

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
            throw new RuntimeException("Error in deleting a chapter");
        }
        return "redirect:/book/" + bookId;
    }
}
