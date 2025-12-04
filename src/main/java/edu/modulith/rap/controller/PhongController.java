package edu.modulith.rap.controller;

import edu.modulith.rap.dto.GheDto;
import edu.modulith.rap.dto.GheUpdateReq;
import edu.modulith.rap.dto.PhongDto;
import edu.modulith.rap.dto.TaoPhongReq;
import edu.modulith.rap.service.PhongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class PhongController {

    private final PhongService phongService;

    // Danh sách phòng theo rạp
    @GetMapping("/rap/{maRap}/phong")
    public List<PhongDto> danhSachPhong(@PathVariable Long maRap) {
        return phongService.danhSachPhongTrongRap(maRap);
    }

    // Tạo phòng trong 1 rạp
    @PostMapping("/rap/{maRap}/phong")
    public PhongDto taoPhong(
            @PathVariable Long maRap,
            @Valid @RequestBody TaoPhongReq req
    ) {
        return phongService.taoPhong(maRap, req);
    }

    // Chi tiết phòng
    @GetMapping("/phong/{maPhong}")
    public PhongDto chiTietPhong(@PathVariable Long maPhong) {
        return phongService.chiTietPhong(maPhong);
    }

    // Cập nhật phòng
    @PutMapping("/phong/{maPhong}")
    public PhongDto capNhatPhong(
            @PathVariable Long maPhong,
            @Valid @RequestBody TaoPhongReq req
    ) {
        return phongService.capNhatPhong(maPhong, req);
    }

    // Xóa phòng
    @DeleteMapping("/phong/{maPhong}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void xoaPhong(@PathVariable Long maPhong) {
        phongService.xoaPhong(maPhong);
    }

    // GHẾ: lấy cấu hình
    @GetMapping("/phong/{maPhong}/ghe")
    public List<GheDto> danhSachGhe(@PathVariable Long maPhong) {
        return phongService.danhSachGhe(maPhong);
    }

    // GHẾ: lưu cấu hình
    @PutMapping("/phong/{maPhong}/ghe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void capNhatGhe(
            @PathVariable Long maPhong,
            @RequestBody List<GheUpdateReq> dsGhe
    ) {
        phongService.capNhatCauHinhGhe(maPhong, dsGhe);
    }
}

