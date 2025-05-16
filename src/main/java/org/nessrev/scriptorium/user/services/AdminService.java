package org.nessrev.scriptorium.user.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nessrev.scriptorium.exception.EmailAlreadyExistsException;
import org.nessrev.scriptorium.user.interfaces.UserRepository;
import org.nessrev.scriptorium.user.interfaces.UserService;
import org.nessrev.scriptorium.user.models.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService implements UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    @Override
    public List<User> getListOfUsers() {
        return userRepository.findAll();
    }

    @Override
    public void createUser(User user) {
        String email = user.getEmail();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
    }

    @Override
    public User getUserByPrincipal(Principal principal) throws AccessDeniedException {
        if (principal == null){
            throw new AccessDeniedException("The user is not authenticated");
        }
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User " + principal.getName() + " not found"));
    }
}
