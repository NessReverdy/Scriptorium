package org.nessrev.scriptorium.image.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ImageInfoDto {
    private Long id;
    private String contentType;
    private String downloadUrl;
}
