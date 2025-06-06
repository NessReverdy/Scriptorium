package org.nessrev.scriptorium.book.mapper;

import org.nessrev.scriptorium.book.dto.BookInfoDto;
import org.nessrev.scriptorium.book.models.Book;

public class BookMapper {
    public static BookInfoDto toBookDto(Book book) {
        return new BookInfoDto(
                book.getId(),
                book.getName(),
                book.getAuthorId()
        );
    }
}
