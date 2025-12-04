package edu.modulith.rap.service;

import edu.modulith.rap.domain.Ghe;
import edu.modulith.rap.domain.GheRepo;
import edu.modulith.rap.domain.Phong;
import edu.modulith.rap.domain.PhongRepo;
import edu.modulith.rap.dto.GheDto;
import edu.modulith.rap.dto.GheLayoutDto;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatConfigService {

    private final GheRepo gheRepo;

    private final PhongRepo phongRepo;

    private final EntityManager entityManager;

    @Transactional
    public GheLayoutDto saveLayout(Long roomId, GheLayoutDto dto) {

        Phong phong = phongRepo.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // xoá hết ghế cũ của phòng
        gheRepo.deleteByPhong_Id(roomId);

        entityManager.flush();

        entityManager.flush();

        List<Ghe> toSave = new ArrayList<>();

        for (GheDto s : dto.gheDTO()) {
            String type = s.loaiGhe();
            if (type == null) continue;

            String upper = type.toUpperCase();
            // EMPTY: ô trống => không tạo bản ghi
            if ("EMPTY".equals(upper)) {
                continue;
            }

            // tạo ghế mới
            Ghe ghe = new Ghe();
            ghe.setPhong(phong);

            // rowIndex -> hang "A","B",...
            int rowIndex = s.hang();
            char rowLetter = (char) ('A' + rowIndex);
            ghe.setHang(String.valueOf(rowLetter));

            // colIndex (0-based) -> cot (1-based)
            int colIndex = s.cot();
            ghe.setCot(colIndex + 1);

            // code từ FE -> nhan_ghe
            ghe.setNhanGhe(s.nhanGhe());     // ví dụ A01

            ghe.setLoaiGhe(mapTypeToLoaiGhe(type));
            ghe.setNhomCouple(s.nhomCouple());
            ghe.setCoupleRole(s.coupleRole());

            ghe.setHoatDong(mapTypeToHoatDong(type));
            ghe.setHeSoGia(mapTypeToHeSoGia(type));

            toSave.add(ghe);
        }

        List<Ghe> saved = gheRepo.saveAll(toSave);

        // map lại ra SeatLayoutDto để FE cập nhật lại state nếu muốn
        List<GheDto> resultSeats = saved.stream().map(ghe -> {
            String hangStr = ghe.getHang();
            int rowIndex = hangStr != null && !hangStr.isEmpty()
                    ? (hangStr.charAt(0) - 'A')
                    : 0;
            int colIndex = (ghe.getCot() != null ? ghe.getCot() : 1) - 1;

            return new GheDto(
                    ghe.getId(),
                    ghe.getNhanGhe(),
                    rowIndex,
                    colIndex,
                    mapLoaiGheToType(ghe),
                    ghe.getNhomCouple(),
                    ghe.getCoupleRole()
            );
        }).toList();

        return new GheLayoutDto(dto.hang(), dto.cot(), resultSeats);
    }

    private BigDecimal mapTypeToHeSoGia(String type) {
        if (type == null) return BigDecimal.ONE;

        return switch (type.toUpperCase()) {
            case "VIP" -> new BigDecimal("1.5");
            case "COUPLE" -> new BigDecimal("2.0");
            default -> BigDecimal.ONE;   // STANDARD/BLOCK
        };
    }

    private String mapLoaiGheToType(Ghe ghe) {
        if (Boolean.FALSE.equals(ghe.getHoatDong())) {
            return "BLOCK";
        }

        String loai = ghe.getLoaiGhe();
        if (loai == null) return "STANDARD";

        return switch (loai.toUpperCase()) {
            case "VIP" -> "VIP";
            case "COUPLE" -> "COUPLE";
            default -> "STANDARD";  // Thường
        };
    }

    private String mapTypeToLoaiGhe(String type) {
        if (type == null) return "Thường";
        return switch (type.toUpperCase()) {
            case "VIP" -> "VIP";
            case "COUPLE" -> "COUPLE";
            default -> "Thường";
        };
    }

    private boolean mapTypeToHoatDong(String type) {
        if (type == null) return true;
        String upper = type.toUpperCase();
        // EMPTY không lưu ghế, BLOCK thì ghế không hoạt động
        return !upper.equals("BLOCK");
    }
}
