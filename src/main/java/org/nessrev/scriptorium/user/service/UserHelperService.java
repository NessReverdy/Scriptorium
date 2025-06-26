package org.nessrev.scriptorium.user.service;

import lombok.AllArgsConstructor;
import org.nessrev.scriptorium.user.repo.UserRepository;
import org.nessrev.scriptorium.user.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.security.Principal;

@Service
@AllArgsConstructor
public class UserHelperService {
    private final UserRepository userRepository;

    @Transactional
    public User getUserByPrincipal(Principal principal) throws AccessDeniedException {
        if (principal == null){
            throw new AccessDeniedException("The user is not authenticated");
        }
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User " + principal.getName() + " not found"));
    }
}
