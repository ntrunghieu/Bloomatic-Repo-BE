package edu.modulith.lichchieu.service;
import edu.modulith.lichchieu.domain.LichChieu;
import edu.modulith.lichchieu.domain.LichChieuRepo;
import edu.modulith.lichchieu.dto.LichChieuDto;
import edu.modulith.lichchieu.dto.SuatChieuDto;
import edu.modulith.phim.domain.Phim;
import edu.modulith.phim.domain.PhimRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LichChieuService {

    private final LichChieuRepo lichChieuRepo;

    private final PhimRepo phimRepo;

    public List<SuatChieuDto> findFilteredSlots(Long maRapChieu, Long maPhong, LocalDate ngayChieu) {

        // 1. GỌI REPOSITORY: Lấy danh sách Entity đã lọc từ Database
        List<LichChieu> lichChieuEntities = lichChieuRepo.findFilteredSuatChieu(
                maRapChieu,
                maPhong,
                ngayChieu
        );

        List<SuatChieuDto> resultDtos = lichChieuEntities.stream()
                .map(SuatChieuDto::fromEntity)
                .collect(Collectors.toList());

        resultDtos.sort(Comparator.comparing(SuatChieuDto::gioBatDau));

        return resultDtos;
    }




    // Phương thức ánh xạ từ Entity sang DTO
    private LichChieuDto convertToDto(LichChieu lichChieu) {
        return new LichChieuDto(
                lichChieu.getMaLichChieu(),
                lichChieu.getPhim().getId(),
                lichChieu.getNgayBatDau(),
                lichChieu.getNgayKetThuc()
        );
    }

    // Phương thức lấy tất cả lịch chiếu dưới dạng DTO
    public List<LichChieuDto> findAllShowtimes() {
        return lichChieuRepo.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public LichChieuDto createShowtime(LichChieuDto dto) {
        int affectedRows = lichChieuRepo.insertShowtimeOnlyDates(
                dto.maPhim(),
                dto.startDate(),
                dto.endDate()
        );
        if (affectedRows > 0) {
            LichChieuDto resultDto = new LichChieuDto(
                    null, // ID sẽ là null hoặc 0
                    dto.maPhim(),
                    dto.startDate(),
                    dto.endDate()
        );
            return resultDto;
        } else {
            throw new RuntimeException("Không thể tạo lịch chiếu bằng Native SQL.");
        }
    }


//    @Transactional
//    public LichChieuDto createShowtime(LichChieuDto dto) {
//        // 1. Lấy Entity Phim (hoặc xử lý lỗi nếu không tìm thấy)
//        Phim phim = phimRepo.findById(dto.maPhim())
//                .orElseThrow(() -> new RuntimeException("Phim không tồn tại!"));
//
//        // 2. Tạo Entity LichChieu (cần Builder hoặc setter)
//        LichChieu newLichChieu = new LichChieu();
//        newLichChieu.setPhim(phim);
//
//        // Đặt ngày bắt đầu và ngày kết thúc từ DTO (đã được đồng bộ với FE)
//        newLichChieu.setNgayBatDau(dto.startDate());
//        newLichChieu.setNgayKetThuc(dto.endDate());
//
//        // Do FE hiện tại chỉ gửi movieId, startDate, endDate,
//        // bạn phải bổ sung giá trị mặc định/lấy từ FE cho các trường bắt buộc khác
//        // Giả sử các trường còn lại được set tạm thời hoặc lấy từ DTO mở rộng
//        // newLichChieu.setPhong(...);
//        // newLichChieu.setGioBatDau(...);
//        // newLichChieu.setGiaCoSo(...);
//
//        LichChieu savedLichChieu = lichChieuRepo.save(newLichChieu);
//        return convertToDto(savedLichChieu);
//    }

    // --- PHƯƠNG THỨC CẬP NHẬT ---
    @Transactional
    public LichChieuDto updateShowtime(Long id, LichChieuDto dto) {
        LichChieu existingLichChieu = lichChieuRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Lịch chiếu không tồn tại!"));

        // Cập nhật Entity Phim (nếu movieId thay đổi)
        if (!existingLichChieu.getPhim().getId().equals(dto.maPhim())) {
            Phim phim = phimRepo.findById(dto.maPhim())
                    .orElseThrow(() -> new RuntimeException("Phim không tồn tại!"));
            existingLichChieu.setPhim(phim);
        }

        // Cập nhật ngày bắt đầu và ngày kết thúc
        existingLichChieu.setNgayBatDau(dto.startDate());
        existingLichChieu.setNgayKetThuc(dto.endDate());

        // Cập nhật các trường khác nếu có (ví dụ: giờ bắt đầu, giá...)
        // existingLichChieu.setGioBatDau(...);

        LichChieu updatedLichChieu = lichChieuRepo.save(existingLichChieu);
        return convertToDto(updatedLichChieu);
    }

    // --- PHƯƠNG THỨC XÓA ---
    @Transactional
    public void deleteShowtime(Long id) {
        if (!lichChieuRepo.existsById(id)) {
            throw new RuntimeException("Lịch chiếu không tồn tại!");
        }
        lichChieuRepo.deleteById(id);
    }
}
