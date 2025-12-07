package edu.modulith.rap.service;

import edu.modulith.phim.domain.Phim;
import edu.modulith.phim.domain.PhimRepo;
import edu.modulith.phim.dto.PhimFilterDto;
import edu.modulith.rap.domain.Phong;
import edu.modulith.rap.domain.PhongRepo;
import edu.modulith.rap.domain.Rap;
import edu.modulith.rap.domain.RapRepo;
import edu.modulith.rap.dto.PhongFilterDto;
import edu.modulith.rap.dto.RapFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataLookupService {

    private final RapRepo rapRepo;
    private final PhongRepo phongRepo;
    private final PhimRepo phimRepo;

    public List<RapFilterDto> getAllCinemas() {
        return rapRepo.findAll().stream()
                .map(RapFilterDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PhongFilterDto> getAllRooms() {
        return phongRepo.findAll().stream()
                .map(PhongFilterDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PhimFilterDto> getAllMovies() {
//        final String TRANG_THAI_DANG_CHIEU = "Đang chiếu";
//        // Lấy ngày hiện tại để so sánh với lịch chiếu của phim
//        final LocalDate currentDate = LocalDate.now();
//
//        // Gọi phương thức mới với trạng thái và ngày hiện tại
//        return phimRepo.findActiveAndScheduledMovies(TRANG_THAI_DANG_CHIEU, currentDate).stream()
//                .map(PhimFilterDto::fromEntity)
//                .collect(Collectors.toList());

        // 1. Định nghĩa các trạng thái cần lọc
        final String TRANG_THAI_DANG_CHIEU = "Đang chiếu";
        final String TRANG_THAI_SAP_CHIEU = "Sắp chiếu";

        // 2. Định nghĩa danh sách trạng thái
        List<String> trangThaisCanLoc = Arrays.asList(TRANG_THAI_DANG_CHIEU, TRANG_THAI_SAP_CHIEU);

        List<Phim> availableMovies = phimRepo.findByTrangThaiIn(trangThaisCanLoc);

        // 4. Chuyển đổi sang DTO và trả về
        return availableMovies.stream()
                .map(PhimFilterDto::fromEntity)
                .collect(Collectors.toList());
    }


}
