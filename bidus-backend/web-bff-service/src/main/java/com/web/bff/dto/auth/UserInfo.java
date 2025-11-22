package com.web.bff.dto.auth;

import com.common.Role;

public record UserInfo (String username, String email, Role role){
}
