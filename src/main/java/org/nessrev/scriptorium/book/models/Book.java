package org.nessrev.scriptorium.book.models;

import jakarta.persistence.*;
import org.nessrev.scriptorium.book.enums.BookStates;
import org.nessrev.scriptorium.chapter.models.Chapter;
import org.nessrev.scriptorium.image.models.BookCover;
import org.nessrev.scriptorium.user.models.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200)
    private String name;
    @Lob
    private String description;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = BookStates.class)
    @Enumerated(EnumType.STRING)

    private Set<BookStates> bookStates = new HashSet<>();
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private User author;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "book")
    private Set<Chapter> chapters = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cover_id", referencedColumnName = "id")
    private BookCover cover;

}
