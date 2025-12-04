package edu.modulith.rap.dto;

public record GheUpdateReq(
        Long maGhe,
        String loaiGhe,
        Boolean hoatDong,
        String nhomCouple
) {}

