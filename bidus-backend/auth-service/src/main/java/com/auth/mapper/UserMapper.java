package com.auth.mapper;

import com.auth.entity.User;
import com.common.UpdateType;
import com.common.dto.user.UserUpdateDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserUpdateDto toUserUpdateDto(User user, UpdateType type) {
        return UserUpdateDto.builder()
                .type(type)
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }

}
