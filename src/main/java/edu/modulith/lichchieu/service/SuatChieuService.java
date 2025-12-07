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
import org.apache.coyote.BadRequestException;
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
    public SuatChieuDto createSlot(SuatChieuDto dto) throws BadRequestException {
        // ... (Lấy Phim, Phòng, Validate Phim/Phòng)

        Phim phim = phimRepo.findById(dto.maPhim()).orElseThrow(
                () -> new BadRequestException("Không tìm thấy phim với mã: " + dto.maPhim())
        );
        Phong phong = phongRepo.findById(dto.maPhong()).orElseThrow(
                () -> new BadRequestException("Không tìm thấy phòng với mã: " + dto.maPhong())
        );

        LocalDate ngayChieu = dto.ngayChieu();
        LocalDateTime gioBatDau = LocalDateTime.of(ngayChieu, dto.gioBatDau());

        // ==========================================================
        // BƯỚC SỬA: XỬ LÝ SUẤT CHIẾU KÉO DÀI QUA NGÀY
        // ==========================================================
        LocalTime thoiDiemKetThuc = dto.gioKetThuc();

        // Khởi tạo thời gian kết thúc bằng ngày bắt đầu + giờ kết thúc
        LocalDateTime gioKetThuc = LocalDateTime.of(ngayChieu, thoiDiemKetThuc);

        // Nếu giờ kết thúc (ví dụ 01:00) nhỏ hơn hoặc bằng giờ bắt đầu (ví dụ 23:00),
        // tức là suất chiếu đã qua ngày mới, cần thêm 1 ngày vào gioKetThuc.
        if (!gioKetThuc.isAfter(gioBatDau)) {
            // Cộng thêm 1 ngày
            gioKetThuc = gioKetThuc.plusDays(1);
        }
        // ==========================================================


        // 1. Validate giờ kết thúc sau giờ bắt đầu (Bây giờ sẽ luôn đúng)
        if (!gioKetThuc.isAfter(gioBatDau)) {
            // Lỗi này chỉ xảy ra nếu có vấn đề nghiêm trọng với logic DTO/giờ
            throw new BadRequestException("Giờ kết thúc phải sau giờ bắt đầu.");
        }

        boolean isOverlapping = lichChieuRepo.existsOverlappingSlot(
                phong.getId(), // Mã phòng
                gioBatDau,     // Giờ bắt đầu của suất mới
                gioKetThuc     // Giờ kết thúc của suất mới
        );

        if (isOverlapping) {
            throw new BadRequestException(
                    "Phòng chiếu này đã có suất chiếu trong khoảng thời gian " +
                            dto.gioBatDau() + " - " + dto.gioKetThuc() + "."
            );
        }

        // 2. Validate phạm vi ngày của phim (Giữ nguyên logic cũ)
        LocalDate ngayKhoiChieu = phim.getNgayKhoiChieu();
        LocalDate ngayKetThuc = phim.getNgayKetThuc();

        if (ngayChieu.isBefore(ngayKhoiChieu) || ngayChieu.isAfter(ngayKetThuc)) {
            throw new BadRequestException(
                    "Ngày chiếu (" + ngayChieu + ") nằm ngoài thời gian phát hành của phim (" +
                            ngayKhoiChieu + " đến " + ngayKetThuc + ")"
            );
        }

        // 4. Tạo entity LichChieu
        LichChieu lichChieu = new LichChieu();
        lichChieu.setPhim(phim);
        lichChieu.setPhong(phong);
        lichChieu.setNgayChieu(ngayChieu); // Lưu ngày bắt đầu
        lichChieu.setGioBatDau(gioBatDau);
        lichChieu.setGioKetThuc(gioKetThuc); // Lưu ngày/giờ kết thúc chính xác (có thể là ngày hôm sau)
        // ... (Các thuộc tính khác)
        lichChieu.setGiaCoSo(dto.giaCoSo());
        lichChieu.setDinhDang(dto.dinhDang());
        lichChieu.setHinhThucDich(dto.hinhThucDich());
        lichChieu.setTrangThai("SCHEDULED");

        LichChieu saved = lichChieuRepo.save(lichChieu);
        return convertToDto(saved);
    }

    @Transactional
//    public SuatChieuDto createSlot(SuatChieuDto dto) {
//        // Lấy Entity Phim và Phong
//        Phim phim = phimRepo.findById(dto.maPhim()).orElseThrow();
//        Phong phong = phongRepo.findById(dto.maPhong()).orElseThrow();
//
//        // Chuyển đổi DTO sang Entity
//        LichChieu lichChieu = new LichChieu();
//        lichChieu.setPhim(phim);
//        lichChieu.setPhong(phong);
//        lichChieu.setNgayChieu(dto.ngayChieu());
////        lichChieu.setNgayKetThuc(dto.ngayBatDau());
//        lichChieu.setGioBatDau(LocalDateTime.of(dto.ngayChieu(), dto.gioBatDau()));
//        lichChieu.setGioKetThuc(LocalDateTime.of(dto.ngayChieu(), dto.gioKetThuc()));
//        lichChieu.setGiaCoSo(dto.giaCoSo());
//        // ... set các trường còn lại ...
//
//        LichChieu saved = lichChieuRepo.save(lichChieu);
//        return convertToDto(saved);
//    }

    public static SuatChieuDto convertToDto(LichChieu entity) {
        // Kiểm tra để tránh NullPointerException nếu các mối quan hệ (phong, rap) là LAZY
        Long maRap = entity.getPhong() != null && entity.getPhong().getRap() != null ?
                entity.getPhong().getRap().getId() : null;
        return new SuatChieuDto(
                entity.getMaLichChieu(),
                maRap,
                entity.getPhim().getId(),
                entity.getPhong() != null ? entity.getPhong().getId() : null,
                entity.getNgayChieu(),
//                entity.getNgayKetThuc(),
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
    public SuatChieuDto updateSlot(Long id, SuatChieuDto dto) throws BadRequestException {
        // 1. Lấy Entity cũ
        LichChieu existingSlot = lichChieuRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy suất chiếu với ID: " + id));

        // Lấy Phim và Phòng (và validate nếu cần)
        Phim phim = phimRepo.findById(dto.maPhim()).orElseThrow(
                () -> new BadRequestException("Không tìm thấy phim với mã: " + dto.maPhim())
        );
        Phong phong = phongRepo.findById(dto.maPhong()).orElseThrow(
                () -> new BadRequestException("Không tìm thấy phòng với mã: " + dto.maPhong())
        );

        // ==========================================================
        // BƯỚC 1: TÁI TẠO GIỜ BẮT ĐẦU VÀ KẾT THÚC CHÍNH XÁC (CÓ QUA NGÀY)
        // ==========================================================
        LocalDate ngayChieu = dto.ngayChieu();
        LocalDateTime gioBatDau = LocalDateTime.of(ngayChieu, dto.gioBatDau());

        LocalTime thoiDiemKetThuc = dto.gioKetThuc();
        LocalDateTime gioKetThuc = LocalDateTime.of(ngayChieu, thoiDiemKetThuc);

        // Logic xử lý suất chiếu qua ngày (giống hệt createSlot)
        if (!gioKetThuc.isAfter(gioBatDau)) {
            // Cộng thêm 1 ngày nếu suất chiếu kết thúc vào ngày hôm sau (ví dụ: 23:00 -> 01:00)
            gioKetThuc = gioKetThuc.plusDays(1);
        }
        // ==========================================================

        // 2. Validate giờ kết thúc sau giờ bắt đầu
        if (!gioKetThuc.isAfter(gioBatDau)) {
            throw new BadRequestException("Giờ kết thúc phải sau giờ bắt đầu.");
        }

        // ==========================================================
        // BƯỚC 2: KIỂM TRA TRÙNG LẶP (LOẠI TRỪ BẢN GHI HIỆN TẠI)
        // ==========================================================
        boolean isOverlapping = lichChieuRepo.existsOverlappingSlotExcludingSelf(
                phong.getId(),      // Mã phòng
                gioBatDau,          // Giờ bắt đầu của suất mới
                gioKetThuc,         // Giờ kết thúc của suất mới
                existingSlot.getMaLichChieu()// ID của suất chiếu đang được CẬP NHẬT
        );

        if (isOverlapping) {
            throw new BadRequestException(
                    "Phòng chiếu này đã có suất chiếu khác trong khoảng thời gian " +
                            dto.gioBatDau() + " - " + dto.gioKetThuc() + "."
            );
        }
        // ==========================================================

        // 3. Validate phạm vi ngày của phim (Giữ nguyên logic cũ)
        LocalDate ngayKhoiChieu = phim.getNgayKhoiChieu();
        LocalDate ngayKetThuc = phim.getNgayKetThuc();
        if (ngayChieu.isBefore(ngayKhoiChieu) || ngayChieu.isAfter(ngayKetThuc)) {
            throw new BadRequestException(
                    "Ngày chiếu (" + ngayChieu + ") nằm ngoài thời gian phát hành của phim (" +
                            ngayKhoiChieu + " đến " + ngayKetThuc + ")"
            );
        }

        // 4. Cập nhật các trường
        existingSlot.setPhim(phim);
        existingSlot.setPhong(phong);
        existingSlot.setNgayChieu(ngayChieu);
        existingSlot.setGioBatDau(gioBatDau);
        existingSlot.setGioKetThuc(gioKetThuc); // Lưu giá trị đã được xử lý qua ngày
        // ... Cập nhật các trường khác:
        existingSlot.setGiaCoSo(dto.giaCoSo());
        existingSlot.setDinhDang(dto.dinhDang());
        existingSlot.setHinhThucDich(dto.hinhThucDich());

        LichChieu updated = lichChieuRepo.save(existingSlot);
        return convertToDto(updated);
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
