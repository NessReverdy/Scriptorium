package org.nessrev.scriptorium.image.mapper;

import lombok.AllArgsConstructor;
import org.nessrev.scriptorium.image.dto.ImageInfoDto;
import org.nessrev.scriptorium.image.model.AllImages;

@AllArgsConstructor
public class ImageMapper {
    public static ImageInfoDto ToImageDto(AllImages img) {
        return new ImageInfoDto(
                img.getId(),
                img.getContentType(),
                "/images/" + img.getId()
        );
    }
}
