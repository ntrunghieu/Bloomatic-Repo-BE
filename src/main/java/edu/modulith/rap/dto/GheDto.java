package edu.modulith.rap.dto;

public record GheDto(
        Long id,
        String nhanGhe,
        int hang,
        int cot,
        String loaiGhe,
        String nhomCouple,
        String coupleRole
) {}

