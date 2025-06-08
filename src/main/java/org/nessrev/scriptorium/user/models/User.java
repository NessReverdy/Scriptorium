package org.nessrev.scriptorium.user.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
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

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Size(max = 1000, message = "Description can't be longer than 1000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean isActive;
    @NotNull(message = "Select your role: Writer or Reader")
    private Boolean isWriter;

    @Column(name = "avatar_id")
    private Long avatarId;

    //todo добавление книг в избранное и отображение избранных книг в акке
    //todo поисковик книг
    //todo добавление видимости главам

    //todo
    //todo
    //todo

}
