package edu.modulith.phim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PhimRequest {

    @NotBlank
    private String tenPhim;

    private String daoDien;

    private String dienVien;

    @NotNull
    private Integer thoiLuong;      // phút

    private String quocGia;

    private LocalDate ngayKhoiChieu;

    private LocalDate ngayKetThuc;

    private String posterUrl;

    private String trailerUrl;

    private String moTa;

    @NotBlank
    private String trangThai;       // Sắp chiếu / Đang chiếu / Đã chiếu

    private String gioiHanTuoi;     // P / T13 / T16 / T18

    // Ví dụ: frontend gửi kèm list id thể loại
    private List<Long> dsMaTheLoai;
}
