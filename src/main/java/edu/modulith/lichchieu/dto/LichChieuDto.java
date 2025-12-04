package edu.modulith.lichchieu.dto;

import java.time.LocalDate;

public record LichChieuDto(
        Long id,
        Long maPhim,
        LocalDate startDate,
        LocalDate endDate
) {}
