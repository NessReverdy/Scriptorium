package org.nessrev.scriptorium.user.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nessrev.scriptorium.user.interfaces.UserService;
import org.nessrev.scriptorium.user.models.User;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WriterService implements UserService {
    @Override
    public List<User> getListOfUsers() {
        return List.of();
    }

    @Override
    public void createUser(User user) {

    }

    @Override
    public User getUserByPrincipal(Principal principal) {
        return null;
    }
}
