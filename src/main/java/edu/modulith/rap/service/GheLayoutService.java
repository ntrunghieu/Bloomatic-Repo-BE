package edu.modulith.rap.service;

import edu.modulith.datve.domain.ChiTietDatVeRepo;
import edu.modulith.lichchieu.domain.LichChieu;
import edu.modulith.lichchieu.domain.LichChieuRepo;
import edu.modulith.rap.domain.Ghe;
import edu.modulith.rap.domain.GheRepo;
import edu.modulith.rap.domain.Phong;
import edu.modulith.rap.dto.GheLayoutUserDto;
import edu.modulith.rap.dto.GheUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GheLayoutService {

    private final LichChieuRepo lichChieuRepo;
    private final GheRepo gheRepo;
    private final ChiTietDatVeRepo chiTietDatVeRepo;

    private final GiuGheService giuGheService;


    public GheLayoutUserDto buildLayoutForShowtime(Long lichChieuId) {
        LichChieu lc = lichChieuRepo.findById(lichChieuId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch chiếu"));

        Phong phong = lc.getPhong();
        Long phongId = phong.getId();

        // 1. Tất cả ghế của phòng
        List<Ghe> gheList = gheRepo.findByPhong_Id(phongId);

        // 2. Danh sách ID ghế đã bị đặt/giữ cho suất chiếu này
        List<Long> bookedSeatIds =
                chiTietDatVeRepo.findBookedSeatIdsByLichChieu(lichChieuId);

        // 3. Map sang DTO cho từng ghế
        List<GheUserDto> gheDtos = gheList.stream()
                .map(g -> {
                    String status;
                    if (bookedSeatIds.contains(g.getId())) {
                        status = "BOOKED";
                    } else if (giuGheService.isHeld(lichChieuId, g.getId())) {
                        status = "HELD";
                    } else {
                        status = "AVAILABLE";
                    }

                    return new GheUserDto(
                            g.getId(),
                            g.getNhanGhe(),
                            toRowIndex(g.getHang()),
                            g.getCot(),
                            g.getLoaiGhe(),
                            g.getNhomCouple(),
                            g.getCoupleRole(),
                            status,
                            g.getHeSoGia()
                    );
                })
                .toList();

        return new GheLayoutUserDto(
                phong.getHang(), // so_hang
                phong.getCot(),  // so_cot
                gheDtos
        );

        // 4. Dùng số hàng / số cột từ Phòng
//        int totalRows = Optional.ofNullable(phong.getHang()).orElse(0); // so_hang
//        int totalCols = Optional.ofNullable(phong.getCot()).orElse(0);  // so_cot
//
//        return new GheLayoutUserDto(
//                totalRows,   // số hàng của phòng
//                totalCols,   // số cột của phòng
//                gheDtos
//        );
    }

    private int toRowIndex(String hang) {
        // 'A' -> 0, 'B' -> 1, ...
        if (hang == null || hang.isEmpty()) return 0;
        return Character.toUpperCase(hang.charAt(0)) - 'A';
    }
}

