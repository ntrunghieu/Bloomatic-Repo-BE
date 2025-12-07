package edu.modulith.rap.dto;

import java.math.BigDecimal;

public record GheUserDto(
        Long id,
        String nhanGhe,
        int hang,
        int cot,
        String loaiGhe,
        String nhomCouple,
        String coupleRole,
        String trangThai,
        BigDecimal heSoGia
) {
}
