package edu.modulith.rap.dto;

import java.time.LocalDateTime;

public record RapDto(
        Long id,
        String tenRap,
        String diaChi,
        String dienThoai,
        String email,
        boolean trangThai,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
