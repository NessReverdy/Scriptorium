package org.nessrev.scriptorium.user.mapper;

import org.nessrev.scriptorium.user.dto.UserInfoDto;
import org.nessrev.scriptorium.user.model.User;

public class UserMapper {
    public static UserInfoDto toUserDto(User user) {
        return new UserInfoDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getAvatarId()
        );
    }
}
