package edu.modulith.rap.dto;

import edu.modulith.lichchieu.dto.LichChieuDto;

import java.util.List;

public record RapLichChieuDTO(
        RapDto rap,
        List<LichChieuDto> lichChieu,
        String thanhPho
) {
}
