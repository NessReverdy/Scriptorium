package org.nessrev.scriptorium.image.controller;

import lombok.RequiredArgsConstructor;
import org.nessrev.scriptorium.image.repo.ImagesRepository;
import org.nessrev.scriptorium.image.model.AllImages;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class ImageController {
    private final ImagesRepository imagesRepository;

    @GetMapping("/api/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        AllImages img = imagesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Avatar not found"));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(img.getContentType()))
                .body(img.getBytes());
    }
}
