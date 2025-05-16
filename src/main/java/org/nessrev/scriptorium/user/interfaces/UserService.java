package org.nessrev.scriptorium.user.interfaces;

import org.nessrev.scriptorium.user.models.User;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

public interface UserService {
    public List<User> getListOfUsers();
    public void createUser(User user);
    public User getUserByPrincipal(Principal principal) throws AccessDeniedException;
}
