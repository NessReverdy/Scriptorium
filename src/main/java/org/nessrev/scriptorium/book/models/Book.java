package org.nessrev.scriptorium.book.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "books")
@Getter
@Setter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200)
    private String name;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Size(max = 1000, message = "Description can't be longer than 1000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;
    @NotNull(message = "Select the visibility of the book: Public or Private")
    private Boolean isPublic;
    @Column(name = "author_id")
    private Long authorId;
    @Column(name = "cover_id")
    private Long coverId;

}
