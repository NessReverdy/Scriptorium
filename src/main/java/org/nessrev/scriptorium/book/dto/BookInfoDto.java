package org.nessrev.scriptorium.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BookInfoDto {
    private Long id;
    private String name;
    private Long authorId;
}
