package edu.modulith.lichchieu.service;

import edu.modulith.lichchieu.domain.LichChieu;
import edu.modulith.lichchieu.domain.LichChieuRepo;
import edu.modulith.lichchieu.dto.*;
import edu.modulith.phim.domain.Phim;
import edu.modulith.phim.domain.PhimRepo;
import edu.modulith.rap.domain.Rap;
import edu.modulith.theloaiphim.domain.PhimTheLoai;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
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
        return lichChieuRepo.findScheduledShowtimesWithoutSpecificDate().stream()
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

    // Cập nhật phương thức: chỉ chấp nhận tenRap và ngayChieu
    public List<RapLichChieuDto> getLichChieuTheoBoLoc(
            String tenRap,
            LocalDate ngayChieu
    ) {
        // 1. Truy vấn tất cả suất chiếu thoả mãn điều kiện lọc
        List<LichChieu> lichChieuList = lichChieuRepo.findSchedulesFiltered(
                ngayChieu,
                tenRap
        );

        // 2. Nhóm LichChieu theo Rạp (Rap)
        Map<Rap, List<LichChieu>> lichChieuTheoRap = lichChieuList.stream()
                // Giả định entity LichChieu có thể truy cập Rap qua Phong
                .collect(Collectors.groupingBy(lc -> lc.getPhong().getRap()));

        // 3. Chuyển đổi từ Map<Rap, List<LichChieu>> sang List<RapScheduleDTO>
        List<RapLichChieuDto> result = new ArrayList<>();

        lichChieuTheoRap.forEach((rap, showtimes) -> {
            // 1. Chuyển đổi List<LichChieu> thành List<LichChieuUserDto>
            List<LichChieuUserDto> lichChieuDTOs = showtimes.stream()
                    .map(this::mapToLichChieuUserDto) // Giả định hàm này trả về LichChieuUserDto
                    .collect(Collectors.toList());

            // 2. Sử dụng CONSTRUCTOR của record để tạo đối tượng RapLichChieuDto
            RapLichChieuDto dto = new RapLichChieuDto(
                    rap.getId(),
                    rap.getTenRap(),
                    rap.getDiaChi(),
                    lichChieuDTOs
            );

            result.add(dto);
        });

        result.sort(Comparator.comparing(RapLichChieuDto::tenRap));
        return result;
    }


//    private LichChieuUserDto mapToLichChieuUserDto(LichChieu lc) {
//        // 1. Chuẩn bị DTO của Phim
//        PhimLichChieuDto phimDto = new PhimLichChieuDto(
//                lc.getPhim().getId(),
//                lc.getPhim().getTenPhim(),
//                lc.getPhim().getPosterUrl(),
//                lc.getPhim().getGioiHanTuoi()
//
//        );
//
//        // 2. Sử dụng Constructor tự động để thiết lập tất cả các giá trị
//        return new LichChieuUserDto(
//                lc.getMaLichChieu(), // maLichChieu
//                lc.getGioBatDau().toLocalTime().toString(), // gioBatDau (đã format "HH:mm")
//                lc.getGiaCoSo(), // giaCoSo
//                lc.getDinhDang(), // dinhDang
//                lc.getHinhThucDich(), // hinhThucDich
//                phimDto // phim
//        );
//    }
//

    private String extractCityFromAddress(String address) {
        if (address.contains("Hà Nội")) return "Hà Nội";
        if (address.contains("Hồ Chí Minh") || address.contains("Tp. HCM")) return "Hồ Chí Minh";
        if (address.contains("Đà Nẵng")) return "Đà Nẵng";
        return "Khác";
    }

    /**
     * Lấy lịch chiếu của tất cả các rạp thuộc một thành phố trong 7 ngày.
     */
    public List<RapLichChieuDto> getSchedules(String city, LocalDate startDate) {
        // Ngày kết thúc (7 ngày kể từ ngày bắt đầu)
        LocalDate endDate = startDate.plusDays(6);

        // 1. Lấy dữ liệu thô từ DB (Repository vẫn cần biến city để lọc)
        List<LichChieu> rawSchedules = lichChieuRepo.findSchedulesByDateRangeAndCityWithDetails(
                startDate, endDate, city
        );

        // 2. Nhóm suất chiếu (LichChieu) theo từng Rạp
        Map<Rap, List<LichChieu>> schedulesByRap = rawSchedules.stream()
                .collect(Collectors.groupingBy(lc -> lc.getPhong().getRap()));

        // 3. Map sang List<RapLichChieuDto> (Sử dụng Record Constructor)
        return schedulesByRap.entrySet().stream().map(entry -> {
            Rap rap = entry.getKey();
            List<LichChieu> lichChieuList = entry.getValue();

            // Map List<LichChieu> sang List<LichChieuUserDto>
            List<LichChieuUserDto> lichChieuUserDtos = lichChieuList.stream()
                    .map(this::mapToLichChieuUserDto)
                    .collect(Collectors.toList());

            // **Sử dụng Constructor của Record RapLichChieuDto**
            return new RapLichChieuDto(
                    rap.getId(), // rapId (Sử dụng ID của Rạp)
                    rap.getTenRap(), // tenRap
                    rap.getDiaChi(), // diaChi
                    lichChieuUserDtos // lichChieu
            );
        }).collect(Collectors.toList());
    }

    private LichChieuUserDto mapToLichChieuUserDto(LichChieu lc) {

        // 1. Tổng hợp Thể loại từ quan hệ PhimTheLoaiSet
        String genres = "";
        if (lc.getPhim().getPhimTheLoaiSet() != null && !lc.getPhim().getPhimTheLoaiSet().isEmpty()) {
            genres = lc.getPhim().getPhimTheLoaiSet().stream()
                    // Lấy tên thể loại (tenTheLoai) từ Entity TheLoaiPhim
                    .map(ptl -> ptl.getTheLoai().getTenTheLoai())
                    // Nối thành chuỗi (VD: "Hành Động, Viễn Tưởng")
                    .collect(Collectors.joining(", "));
        }

        // 2. Chuẩn bị DTO của Phim (đã bổ sung theLoai)
        PhimLichChieuDto phimDto = new PhimLichChieuDto(
                lc.getPhim().getId(),
                lc.getPhim().getTenPhim(),
                lc.getPhim().getPosterUrl(),
                lc.getPhim().getGioiHanTuoi(),
                genres // TRUYỀN THỂ LOẠI ĐÃ TỔNG HỢP
        );

        // 3. Sử dụng record Constructor để thiết lập 9 giá trị
        return new LichChieuUserDto(
                lc.getMaLichChieu(), // maLichChieu
                lc.getPhong().getId(), // maPhong (Lấy từ ID của Phong)
                lc.getNgayChieu(), // ngayBatDau (LocalDate)
                lc.getGioBatDau().toLocalTime().toString(), // gioBatDau (String "HH:mm:ss")
                lc.getGioKetThuc().toLocalTime().toString(), // gioKetThuc (String "HH:mm:ss")
                lc.getGiaCoSo(), // giaCoSo
                lc.getDinhDang(), // dinhDang
                lc.getHinhThucDich(), // hinhThucDich
                phimDto // phim
        );
    }
}

