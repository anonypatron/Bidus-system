package com.generator.dto;

public record SignupRequestDto(
        String email,
        String username,
        String password,
        String role
) {}
