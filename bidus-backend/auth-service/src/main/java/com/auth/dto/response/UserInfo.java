package com.auth.dto.response;

import com.common.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfo {

    private String username;
    private String email;
    private Role role;

    @Builder
    public UserInfo(String username, String email, Role role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

}
