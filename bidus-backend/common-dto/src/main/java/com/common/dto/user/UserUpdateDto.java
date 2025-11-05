package com.common.dto.user;

import com.common.Role;
import com.common.UpdateType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserUpdateDto {

    private UpdateType type;
    private Long id;
    private String email;
    private String username;
    private String password;
    private Role role;

    @Builder
    public UserUpdateDto(
            UpdateType type,
            Long id,
            String email,
            String username,
            String password,
            Role role) {
        this.type = type;
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

}
