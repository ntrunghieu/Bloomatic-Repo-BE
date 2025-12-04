package edu.modulith.rap.dto;

import java.time.LocalDateTime;

public record PhongDto(
        Long maPhong,
        Long maRap,
        String tenPhong,
        String loaiPhong,
        boolean trangThai,
        Integer hang,
        Integer cot,

//        long tongSoGhe,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

