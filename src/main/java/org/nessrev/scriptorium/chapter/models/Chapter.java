package org.nessrev.scriptorium.chapter.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.nessrev.scriptorium.chapter.enums.ChapterStates;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chapters")
@Getter
@Setter
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String title;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "TEXT")
    private String description;
    @ElementCollection(fetch = FetchType.EAGER, targetClass = ChapterStates.class)
    private Set<ChapterStates> chapterStates = new HashSet<>();

    @Column(name = "book_id")
    private Long bookId;
}
