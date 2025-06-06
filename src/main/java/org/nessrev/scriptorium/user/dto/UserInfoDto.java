package org.nessrev.scriptorium.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserInfoDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Long avatarId;
}
