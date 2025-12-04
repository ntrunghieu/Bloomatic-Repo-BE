package edu.modulith.phim.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PhimDto {

    private Long id;

    private String tenPhim;

    private String daoDien;

    private String dienVien;

    private Integer thoiLuong;

    private String quocGia;

    private LocalDate ngayKhoiChieu;

    private LocalDate ngayKetThuc;

    private String posterUrl;

    private String trailerUrl;

    private String moTa;

    private String trangThai;

    private String gioiHanTuoi;

    private List<String> dsMaTheLoai;

    private LocalDateTime createdAt;
}
