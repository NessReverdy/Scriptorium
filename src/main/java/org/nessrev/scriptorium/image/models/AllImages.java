package org.nessrev.scriptorium.image.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "imagesTb")
@Getter
@Setter
public class AllImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String originalFileName;
    private Long size;
    private String contentType;
    @Column(columnDefinition = "bytea")
    private byte[] bytes;
}
