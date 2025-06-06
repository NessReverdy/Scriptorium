package org.nessrev.scriptorium.user.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nessrev.scriptorium.book.models.Book;
import org.nessrev.scriptorium.book.services.BookService;
import org.nessrev.scriptorium.exception.EmailAlreadyExistsException;
import org.nessrev.scriptorium.image.dto.ImageInfoDto;
import org.nessrev.scriptorium.image.interfaces.ImagesRepository;
import org.nessrev.scriptorium.image.mapper.ImageMapper;
import org.nessrev.scriptorium.user.interfaces.UserRepository;
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
public class UserController {
    private final UserService userService;
    private final UserHelperService userHelperService;
    private final BookService bookService;
    private final UserRepository userRepository;

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
        User user = userService.getUserById(id);
        User chekingUser = userHelperService.getUserByPrincipal(principal);

        if(user.getEmail().equals(chekingUser.getEmail())) {
            return "redirect:/my-profile";
        }

        List<Book> books = bookService.getAllBooksById(user.getId());

        model.addAttribute("books", books);
        model.addAttribute("user", user);
        model.addAttribute("avatarPath", user.getAvatarId() != null
                ? "/api/images/" + user.getAvatarId()
                : "/images/defaultImg.jpg");
        return "profile-info";
    }
}
