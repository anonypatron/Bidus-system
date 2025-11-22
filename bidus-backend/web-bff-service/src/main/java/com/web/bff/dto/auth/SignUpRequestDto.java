package com.web.bff.dto.auth;

import com.common.Role;

public record SignUpRequestDto (
        String email,
        String username,
        String password,
        Role role
) {
}
