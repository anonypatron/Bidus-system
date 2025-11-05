package com.auth.dto.request;

import com.common.Role;
import lombok.Getter;

@Getter
public class SignUpRequestDto {

    private String email;
    private String username;
    private String password;
    private Role role;

}
