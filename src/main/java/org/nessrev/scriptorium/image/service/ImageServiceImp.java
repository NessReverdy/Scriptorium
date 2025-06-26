package org.nessrev.scriptorium.image.service;

import lombok.RequiredArgsConstructor;
import org.nessrev.scriptorium.image.dto.ImageInfoDto;
import org.nessrev.scriptorium.image.repo.ImagesRepository;
import org.nessrev.scriptorium.image.mapper.ImageMapper;
import org.nessrev.scriptorium.image.model.AllImages;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageServiceImp implements ImageService {
    private final ImagesRepository imagesRepository;

    @Override
    public ImageInfoDto save(MultipartFile file){
        try{
            AllImages avatar = new AllImages();
            avatar.setName(file.getName());
            avatar.setOriginalFileName(file.getOriginalFilename());
            avatar.setSize(file.getSize());
            avatar.setContentType(file.getContentType());
            avatar.setBytes(file.getBytes());

            AllImages saved = imagesRepository.save(avatar);

            return ImageMapper.ToImageDto(saved);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save avatar", e);
        }
    }

}
