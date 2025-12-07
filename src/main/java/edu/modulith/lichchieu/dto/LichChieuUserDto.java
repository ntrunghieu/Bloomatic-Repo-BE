package edu.modulith.lichchieu.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LichChieuUserDto(
        Long maLichChieu,
        Long maPhong,
        LocalDate ngayChieu,
        String gioBatDau,
        String gioKetThuc,
        BigDecimal giaCoSo,
        String dinhDang,
        String hinhThucDich,
        PhimLichChieuDto phim
) {
}
