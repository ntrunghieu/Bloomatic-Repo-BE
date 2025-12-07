package edu.modulith.lichchieu.dto;

import java.util.List;

public record RapLichChieuDto(
         Long rapId,
         String tenRap,
         String diaChi,
         List<LichChieuUserDto> lichChieu
) {
}
