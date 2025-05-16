package org.nessrev.scriptorium.user.models;

import jakarta.persistence.*;
import lombok.Getter;
import org.nessrev.scriptorium.book.models.Book;
import org.nessrev.scriptorium.image.models.UserAvatar;
import org.nessrev.scriptorium.user.enums.UserRoles;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String firstName;


    @Column(nullable = false, length = 60)
    private String lastName;

    @Getter
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, length = 40)
    private String password;

    @Lob
    private String description;

    private boolean isActive;

    @ElementCollection(targetClass = UserRoles.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<UserRoles> roles = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author")
    private Set<Book> books = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "avatar_id", referencedColumnName = "id")
    private UserAvatar avatar;
}
