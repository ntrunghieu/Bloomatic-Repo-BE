package edu.modulith.auth.dto;

import java.util.Set;

public record LoginRes(
        String accessToken,
        Long   userId,
        String fullName,
        String email,
        Set<String> roles
) {}