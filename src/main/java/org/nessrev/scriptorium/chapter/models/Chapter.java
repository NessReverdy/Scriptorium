package org.nessrev.scriptorium.chapter.models;

import jakarta.persistence.*;
import org.nessrev.scriptorium.book.models.Book;
import org.nessrev.scriptorium.chapter.enums.ChapterStates;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chapters")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String title;
    @Lob
    private String description;
    @ElementCollection(fetch = FetchType.EAGER, targetClass = ChapterStates.class)
    private Set<ChapterStates> chapterStates = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Book book;
}
