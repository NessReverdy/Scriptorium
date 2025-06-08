package org.nessrev.scriptorium.user.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nessrev.scriptorium.book.models.Book;
import org.nessrev.scriptorium.book.services.BookService;
import org.nessrev.scriptorium.user.models.User;
import org.nessrev.scriptorium.user.services.UserHelperService;
import org.nessrev.scriptorium.user.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserHelperService userHelperService;
    private final BookService bookService;

    @GetMapping("/login")
    public String login(Principal principal, Model model) throws AccessDeniedException {
        if (principal != null) {
            User user = userHelperService.getUserByPrincipal(principal);
            model.addAttribute("user", user);
        }
        return "login";
    }

    @GetMapping("/registration")
    public String registration(Principal principal, Model model) throws AccessDeniedException {
        if (principal != null) {
            User user = userHelperService.getUserByPrincipal(principal);
            model.addAttribute("user", user);
            return "redirect:/main";
        }
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String createUser(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult,
                             @RequestParam("avatarFile") MultipartFile avatarFile,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        if (principal != null) {
            return "redirect:/main";
        }

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        if (!userService.createUser(user, avatarFile)) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ User with email " + user.getEmail() + " already exists");
            return "redirect:/registration?error";
        }

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        return "redirect:/login";
    }

    @GetMapping("/my-profile")
    public String myProfilePage(Principal principal, Model model) throws AccessDeniedException {
        User user = userHelperService.getUserByPrincipal(principal);
        List<Book> books = bookService.getAllBooksById(user.getId());

        model.addAttribute("books", books);
        model.addAttribute("user", user);
        model.addAttribute("avatarPath", user.getAvatarId() != null
                ? "/api/images/" + user.getAvatarId()
                : "/images/defaultImg.jpg");
        return "my-profile";
    }

    @GetMapping("/profile-info/{id}")
    public String profileInfo(@PathVariable("id") Long id, Model model, Principal principal) throws AccessDeniedException {
        User justUser = userService.getUserById(id);
        User chekingUser = userHelperService.getUserByPrincipal(principal);

        if(justUser.getEmail().equals(chekingUser.getEmail())) {
            return "redirect:/my-profile";
        }

        List<Book> books = bookService.getAllBooksById(justUser.getId());

        model.addAttribute("user", chekingUser);
        model.addAttribute("books", books);
        model.addAttribute("justUser", justUser);
        model.addAttribute("avatarPath", justUser.getAvatarId() != null
                ? "/api/images/" + justUser.getAvatarId()
                : "/images/defaultImg.jpg");
        return "profile-info";
    }

    @PostMapping("/user/edit/{id}")
    public String editUser(@PathVariable Long id,
                           @RequestParam(required = false) String firstName,
                           @RequestParam(required = false) String lastName,
                           @RequestParam(required = false) String description,
                           @RequestParam(required = false) String email,
                           @RequestParam(required = false) MultipartFile avatar,
                           @RequestParam("isWriter") boolean isWriter,
                           HttpServletRequest request,
                           Principal principal) throws AccessDeniedException {
        User existingUser = userHelperService.getUserByPrincipal(principal);
        String userEmail = existingUser.getEmail();

        if (!userService.editUser(existingUser, firstName, lastName, description, email, avatar, isWriter)) {
            log.error("Cannot to edit the user");
        }

        if (!userEmail.equalsIgnoreCase(existingUser.getEmail())) {
            request.getSession().invalidate();
            return "redirect:/logout";
        }
        return "redirect:/my-profile";
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpServletRequest request){
        if ((!bookService.deleteBooksByUserId(id)) & (!userService.deleteUser(id))) {
            log.error("Cannot to delete the user");
        }
        request.getSession().invalidate();
        return "redirect:/logout";
    }
}
