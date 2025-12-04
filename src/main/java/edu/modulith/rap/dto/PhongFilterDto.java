package edu.modulith.rap.dto;

import edu.modulith.rap.domain.Phong;

public record PhongFilterDto(
        Long id, Long maRap, String tenPhong
) {
    public static PhongFilterDto fromEntity(Phong phong) {
        return new PhongFilterDto(
                phong.getId(),
                phong.getRap().getId(), // Lấy ma_rap thông qua quan hệ
                phong.getTenPhong()
        );
    }
}
