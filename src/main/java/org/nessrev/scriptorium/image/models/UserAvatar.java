package org.nessrev.scriptorium.image.models;

import jakarta.persistence.*;
import org.nessrev.scriptorium.user.models.User;

@Entity
@Table(name = "users-avatars")
public class UserAvatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String originalFileName;
    private Long size;
    private String contentType;
    @Lob
    private byte[] bytes;

    @OneToOne(mappedBy = "avatar")
    private User user;
}
