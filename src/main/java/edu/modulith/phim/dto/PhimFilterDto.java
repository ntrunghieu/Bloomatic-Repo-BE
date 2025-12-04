package edu.modulith.phim.dto;

import edu.modulith.phim.domain.Phim;

public record PhimFilterDto(
        Long id, String tenPhim, String trangThai, Integer thoiLuong
) {
    public static PhimFilterDto fromEntity(Phim phim) {
        return new PhimFilterDto(
                phim.getId(),
                phim.getTenPhim(),
                phim.getTrangThai(),
                phim.getThoiLuong()
        );
    }
}
