package org.nessrev.scriptorium.user.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nessrev.scriptorium.image.dto.ImageInfoDto;
import org.nessrev.scriptorium.image.service.ImageService;
import org.nessrev.scriptorium.image.repo.ImagesRepository;
import org.nessrev.scriptorium.image.mapper.ImageMapper;
import org.nessrev.scriptorium.user.dto.UserInfoDto;
import org.nessrev.scriptorium.user.repo.UserRepository;
import org.nessrev.scriptorium.user.mapper.UserMapper;
import org.nessrev.scriptorium.user.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final UserHelperService userHelperService;
    private final ImagesRepository imagesRepository;

    public List<User> getListOfUsers() {
        return userRepository.findAll();
    }

    public boolean createUser(User user, @RequestParam("avatarFile") MultipartFile avatarFile) {
        String email = user.getEmail();
        if (userRepository.findByEmail(email).isPresent()) {
            return false;
        }

        ImageInfoDto avatarInfo = imageService.save(avatarFile);
        user.setAvatarId(avatarInfo.getId());

        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        log.info("Created user with email: {}", user.getEmail());
        return true;
    }

    public UserInfoDto saveAuthor (Principal principal){
        try {
            User user = userHelperService.getUserByPrincipal(principal);
            return UserMapper.toUserDto(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save author", e);
        }
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    public ImageInfoDto getUserAvatar(Long id) {
        ImageInfoDto avatarDto;
        if (id != null) {
            avatarDto = imagesRepository.findById(id)
                    .map(ImageMapper::ToImageDto)
                    .orElseThrow(() -> new RuntimeException("Avatar not found"));
            return avatarDto;
        }
        return null;
    }

    public boolean editUser(User user,
                            String firstName,
                            String lastName,
                            String description,
                            String email,
                            @RequestParam("avatar") MultipartFile avatarFile,
                            boolean isWriter) {
        if (firstName != null && !firstName.trim().isEmpty() ) {
            user.setFirstName(firstName);
        }
        if (lastName != null && !lastName.trim().isEmpty() ) {
            user.setLastName(lastName);
        }
        if (description != null && !description.trim().isEmpty() ) {
            user.setDescription(description);
        }
        if (email != null && !email.trim().isEmpty() ) {
            user.setEmail(email);
        }
        if (avatarFile != null && !avatarFile.isEmpty()) {
            imagesRepository.deleteById(user.getAvatarId());
            ImageInfoDto avatarInfo = imageService.save(avatarFile);
            user.setAvatarId(avatarInfo.getId());
        }
        user.setIsWriter(isWriter);
        userRepository.save(user);
        log.info("User edited successfully");
        return true;
    }

    public boolean deleteUser(Long userId) {
        User user = getUserById(userId);
        imagesRepository.deleteById(user.getAvatarId());
        userRepository.delete(user);
        log.info("User deleted successfully");
        return true;
    }
}
