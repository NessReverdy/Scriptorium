package org.nessrev.scriptorium.image.service;

import org.nessrev.scriptorium.image.dto.ImageInfoDto;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    ImageInfoDto save(MultipartFile file);
}
