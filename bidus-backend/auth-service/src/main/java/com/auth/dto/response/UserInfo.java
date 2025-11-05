package com.auth.dto.response;

import com.common.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfo {

    private String username;
    private Role role;

    @Builder
    public UserInfo(String username, Role role) {
        this.username = username;
        this.role = role;
    }

}
