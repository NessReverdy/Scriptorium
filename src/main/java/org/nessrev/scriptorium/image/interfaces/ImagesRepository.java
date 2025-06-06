package org.nessrev.scriptorium.image.interfaces;

import org.nessrev.scriptorium.image.models.AllImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagesRepository extends JpaRepository<AllImages, Long> {
    AllImages findById(long id);

    Long findByOriginalFileName(String coverName);
}
