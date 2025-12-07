package edu.modulith.phim.service;
import edu.modulith.phim.domain.Phim;
import edu.modulith.phim.domain.PhimRepo;
import edu.modulith.phim.dto.PhimDto;
import edu.modulith.phim.dto.PhimFilterDto;
import edu.modulith.phim.dto.PhimOption;
import edu.modulith.phim.dto.PhimRequest;
import edu.modulith.theloaiphim.domain.PhimTheLoai;
import edu.modulith.theloaiphim.domain.PhimTheLoaiId;
import edu.modulith.theloaiphim.domain.TheLoaiPhim;
import edu.modulith.theloaiphim.dto.PhimTheLoaiRepository;
import edu.modulith.theloaiphim.dto.TheLoaiPhimRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhimService {

    private final PhimRepo phimRepository;
    private final TheLoaiPhimRepository theLoaiPhimRepository;

    private final PhimTheLoaiRepository phimTheLoaiRepository;

    @Transactional
    public Page<PhimDto> timKiemPhim(String trangThai, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Phim> pagePhim = phimRepository.timKiemPhim(trangThai, pageable);

        return pagePhim.map(phim -> {
            PhimDto dto = toDto(phim);

            List<String> tenTheLoai = theLoaiPhimRepository
                    .findTenTheLoaiByMaPhim(phim.getId());

            dto.setDsMaTheLoai(tenTheLoai);
            return dto;
        });
    }

    public PhimDto chiTietPhim(Long id) {
        Phim phim = phimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));
        return toDto(phim);
    }

    public PhimDto themPhim(PhimRequest req) {
        Phim phim = new Phim();
        phim.setTenPhim(req.getTenPhim());
        phim.setDaoDien(req.getDaoDien());
        phim.setDienVien(req.getDienVien());
        phim.setThoiLuong(req.getThoiLuong());
        phim.setQuocGia(req.getQuocGia());
        phim.setNgayKhoiChieu(req.getNgayKhoiChieu());
        phim.setNgayKetThuc(req.getNgayKetThuc());
        phim.setPosterUrl(req.getPosterUrl());
        phim.setTrailerUrl(req.getTrailerUrl());
        phim.setMoTa(req.getMoTa());
        phim.setTrangThai(req.getTrangThai());
        phim.setGioiHanTuoi(req.getGioiHanTuoi());
        // TODO: xử lý dsMaTheLoai -> bảng phim_the_loai

        phim = phimRepository.save(phim);
        return toDto(phim);
    }

    @Transactional
    public PhimDto capNhatPhim(Long id, PhimRequest req) {
        final Phim phim = phimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));

        phim.setTenPhim(req.getTenPhim());
        phim.setDaoDien(req.getDaoDien());
        phim.setDienVien(req.getDienVien());
        phim.setThoiLuong(req.getThoiLuong());
        phim.setQuocGia(req.getQuocGia());
        phim.setNgayKhoiChieu(req.getNgayKhoiChieu());
        phim.setNgayKetThuc(req.getNgayKetThuc());
        phim.setPosterUrl(req.getPosterUrl());
        phim.setTrailerUrl(req.getTrailerUrl());
        phim.setMoTa(req.getMoTa());
        phim.setTrangThai(req.getTrangThai());
        phim.setGioiHanTuoi(req.getGioiHanTuoi());
        // TODO: cập nhật dsMaTheLoai

        phimTheLoaiRepository.deleteByPhim_Id(phim.getId());

        List<Long> maTheLoaiIds = req.getDsMaTheLoai(); // Giả định List<Long>
        List<TheLoaiPhim> newTheLoaiList = theLoaiPhimRepository.findAllById(maTheLoaiIds);

        List<PhimTheLoai> newRelations = newTheLoaiList.stream()
                .map(theLoai -> PhimTheLoai.builder()
                        .id(new PhimTheLoaiId(phim.getId(), theLoai.getId())) // Tạo khóa phức hợp
                        .phim(phim)
                        .theLoai(theLoai)
                        .build()
                )
                .collect(Collectors.toList());

        phimTheLoaiRepository.saveAll(newRelations);

        Phim savedPhim = phimRepository.save(phim);
        return toDto(savedPhim);
    }

    @Transactional
    private PhimDto toDto(Phim phim) {
        PhimDto dto = new PhimDto();
        List<PhimTheLoai> relations = phimTheLoaiRepository.findByPhim_Id(phim.getId());

        List<String> dsTenTheLoai = relations.stream()
                .map(r -> r.getTheLoai().getTenTheLoai())
                .collect(Collectors.toList());

        dto.setId(phim.getId());
        dto.setTenPhim(phim.getTenPhim());
        dto.setDaoDien(phim.getDaoDien());
        dto.setDienVien(phim.getDienVien());
        dto.setThoiLuong(phim.getThoiLuong());
        dto.setQuocGia(phim.getQuocGia());
        dto.setNgayKhoiChieu(phim.getNgayKhoiChieu());
        dto.setNgayKetThuc(phim.getNgayKetThuc());
        dto.setPosterUrl(phim.getPosterUrl());
        dto.setTrailerUrl(phim.getTrailerUrl());
        dto.setMoTa(phim.getMoTa());
        dto.setTrangThai(phim.getTrangThai());
        dto.setGioiHanTuoi(phim.getGioiHanTuoi());
        dto.setCreatedAt(phim.getCreatedAt());
        dto.setDsMaTheLoai(dsTenTheLoai);

        return dto;
    }

    private PhimOption convertToMovieOption(Phim phim) {
        // Giả định Entity Phim có phương thức getMaPhim() và getTenPhim()
        return new PhimOption(phim.getId(), phim.getTenPhim());
    }

//    public List<PhimOption> findAvailableMovies() {
//        LocalDate today = LocalDate.now();
//
//        // Gọi custom query: Lấy phim có ngayKhoiChieu >= today
//        List<Phim> availableMovies = phimRepository.findByNgayKhoiChieuGreaterThanEqual(today);
//
//        // Ánh xạ sang DTO và trả về
//        return availableMovies.stream()
//                .map(this::convertToMovieOption)
//                .collect(Collectors.toList());
//    }

    public List<PhimOption> findAvailableMovies() {
        // 1. Định nghĩa các trạng thái cần lọc
        final String TRANG_THAI_DANG_CHIEU = "Đang chiếu";
        final String TRANG_THAI_SAP_CHIEU = "Sắp chiếu";

        // 2. Định nghĩa danh sách trạng thái
        List<String> trangThaisCanLoc = Arrays.asList(TRANG_THAI_DANG_CHIEU, TRANG_THAI_SAP_CHIEU);

        List<Phim> availableMovies = phimRepository.findByTrangThaiIn(trangThaisCanLoc);

        // 4. Chuyển đổi sang DTO và trả về
        return availableMovies.stream()
                .map(this::convertToMovieOption)
                .collect(Collectors.toList());
    }

//    public List<PhimOption> findAvailableMovies() {
//        List<Phim> availableMovies = phimRepository.findAll();
//
//        return availableMovies.stream()
//                .map(this::convertToMovieOption)
//                .collect(Collectors.toList());
//    }

//    public List<PhimFilterDto> getAllMovies() {
//        final String TRANG_THAI_DANG_CHIEU = "Đang chiếu";
//        // Lấy ngày hiện tại để so sánh với lịch chiếu của phim
//        final LocalDate currentDate = LocalDate.now();
//
//        // Gọi phương thức mới với trạng thái và ngày hiện tại
//        return phimRepository.findActiveAndScheduledMovies(TRANG_THAI_DANG_CHIEU, currentDate).stream()
//                .map(PhimFilterDto::fromEntity)
//                .collect(Collectors.toList());
//    }
}


