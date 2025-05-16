package org.nessrev.scriptorium.image.interfaces;

import org.nessrev.scriptorium.image.models.UserAvatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAvatarRepository extends JpaRepository<UserAvatar, Long> {

}
