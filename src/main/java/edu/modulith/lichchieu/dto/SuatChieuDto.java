package edu.modulith.lichchieu.dto;

import edu.modulith.lichchieu.domain.LichChieu;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record SuatChieuDto(
        Long id,
        Long maRap,
        Long maPhim,
        Long maPhong,
        LocalDate ngayBatDau,
        LocalDate ngayKetThuc,
        String dinhDang,
        String hinhThucDich,
        LocalTime gioBatDau,
        LocalTime gioKetThuc,
        String trangThai,
        BigDecimal giaCoSo
) {

    public static SuatChieuDto fromEntity(LichChieu entity) {

        // Kiểm tra để tránh NullPointerException nếu các mối quan hệ (phong, rap) là LAZY
        Long maRap = entity.getPhong() != null && entity.getPhong().getRap() != null ?
                entity.getPhong().getRap().getId() : null;

        return new SuatChieuDto(
                entity.getMaLichChieu(),
                maRap,
                entity.getPhim().getId(),
                entity.getPhong() != null ? entity.getPhong().getId() : null,
                entity.getNgayBatDau(),
                entity.getNgayKetThuc(),
                entity.getDinhDang(),
                entity.getHinhThucDich(),
                entity.getGioBatDau() != null ? entity.getGioBatDau().toLocalTime() : null,
                entity.getGioKetThuc() != null ? entity.getGioKetThuc().toLocalTime() : null,
                entity.getTrangThai(),
                entity.getGiaCoSo()
        );
    }

}
