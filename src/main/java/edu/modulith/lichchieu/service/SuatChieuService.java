package edu.modulith.lichchieu.service;

import edu.modulith.common.exception.ResourceNotFoundException;
import edu.modulith.lichchieu.domain.LichChieu;
import edu.modulith.lichchieu.domain.LichChieuRepo;
import edu.modulith.lichchieu.dto.SuatChieuDto;
import edu.modulith.phim.domain.Phim;
import edu.modulith.phim.domain.PhimRepo;
import edu.modulith.rap.domain.Phong;
import edu.modulith.rap.domain.PhongRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuatChieuService {
    private final LichChieuRepo lichChieuRepo;
    // Cần Repository cho Phim và Phong để lấy Entity
    private final PhimRepo phimRepo;
    private final PhongRepo phongRepo;


//    public List<LichChieu> findFilteredSuatChieu(Long cinemaId, Long roomId, LocalDate date) {
//        List<LichChieu> suatChieuDto = lichChieuRepo.findFilteredSuatChieu(cinemaId, roomId, date);
//        return suatChieuDto;
//    }

    // 2. Tạo suất chiếu
    @Transactional
    public SuatChieuDto createSlot(SuatChieuDto dto) {
        // Lấy Entity Phim và Phong
        Phim phim = phimRepo.findById(dto.maPhim()).orElseThrow();
        Phong phong = phongRepo.findById(dto.maPhong()).orElseThrow();

        // Chuyển đổi DTO sang Entity
        LichChieu lichChieu = new LichChieu();
        lichChieu.setPhim(phim);
        lichChieu.setPhong(phong);
        lichChieu.setNgayBatDau(dto.ngayBatDau());
        lichChieu.setNgayKetThuc(dto.ngayBatDau()); // Set ngày kết thúc bằng ngày bắt đầu cho suất chiếu cụ thể
        lichChieu.setGioBatDau(LocalDateTime.of(dto.ngayBatDau(), dto.gioBatDau())); // Kết hợp Date và Time
        lichChieu.setGioKetThuc(LocalDateTime.of(dto.ngayBatDau(), dto.gioKetThuc()));
        lichChieu.setGiaCoSo(dto.giaCoSo());
        // ... set các trường còn lại ...

        LichChieu saved = lichChieuRepo.save(lichChieu);
        return convertToDto(saved);
    }

    public static SuatChieuDto convertToDto(LichChieu entity) {
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
    // 3. Cập nhật và Xóa tương tự
    @Transactional
    public SuatChieuDto updateSlot(Long id, SuatChieuDto dto) throws ResourceNotFoundException {

        // 1. Kiểm tra sự tồn tại của các mối quan hệ (vẫn cần thiết)
        // Nếu không tồn tại Phim hoặc Phòng, ném lỗi 400 Bad Request
        phimRepo.findById(dto.maPhim())
                .orElseThrow(() -> new DataIntegrityViolationException("Mã Phim không hợp lệ: " + dto.maPhim()));

        phongRepo.findById(dto.maPhong())
                .orElseThrow(() -> new DataIntegrityViolationException("Mã Phòng không hợp lệ: " + dto.maPhong()));

        // 2. Chuẩn bị dữ liệu LocalDateTime
        LocalDate ngayChieu = dto.ngayBatDau();
        LocalTime gioBatDau = dto.gioBatDau();
        LocalTime gioKetThuc = dto.gioKetThuc();

        // Kết hợp Ngày và Giờ thành LocalDateTime
        LocalDateTime gioBatDauLDT = LocalDateTime.of(ngayChieu, gioBatDau);
        LocalDateTime gioKetThucLDT = LocalDateTime.of(ngayChieu, gioKetThuc);

        // 3. GỌI NATIVE QUERY
        int updatedRows = lichChieuRepo.updateSlotNative(
                id,
                dto.maPhim(),
                dto.maPhong(),
                dto.ngayBatDau(),
                dto.ngayKetThuc(),
                dto.dinhDang(),
                dto.hinhThucDich(),
                gioBatDauLDT,
                gioKetThucLDT,
                dto.trangThai(),
                dto.giaCoSo()
        );

        // 4. Xử lý trường hợp không tìm thấy ID (updatedRows = 0)
        if (updatedRows == 0) {
            // Kiểm tra xem ID suất chiếu có tồn tại không
            if (!lichChieuRepo.existsById(id)) {
                throw new ResourceNotFoundException("Không tìm thấy suất chiếu với ID: " + id);
            }
        }

        // 5. Trả về DTO đã cập nhật
        // Phương pháp an toàn nhất là tải lại Entity để đảm bảo dữ liệu mới nhất
        LichChieu updatedSlot = lichChieuRepo.findById(id).get();

        return SuatChieuDto.fromEntity(updatedSlot);
    }

    public void deleteSlot(Long id) throws ResourceNotFoundException {
        // 1. Kiểm tra sự tồn tại (Tùy chọn, nhưng nên có để đảm bảo báo lỗi 404)
        if (!lichChieuRepo.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy suất chiếu với ID: " + id);
        }

        // 2. Xóa suất chiếu
        lichChieuRepo.deleteById(id);
    }
}
