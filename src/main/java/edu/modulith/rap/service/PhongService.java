package edu.modulith.rap.service;

import edu.modulith.rap.domain.*;
import edu.modulith.rap.dto.GheDto;
import edu.modulith.rap.dto.GheUpdateReq;
import edu.modulith.rap.dto.PhongDto;
import edu.modulith.rap.dto.TaoPhongReq;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhongService {

    private final PhongRepo phongRepo;
    private final GheRepo gheRepo;
    private final RapRepo rapRepo;

    private PhongDto mapToDto(Phong p) {
//        GheRepo.PhongSeatStats stats = gheRepo.tinhThongKePhong(p.getId());
//        long soHang = stats != null ? stats.getSoHang() : 0;
//        int soCot = (stats != null && stats.getSoCot() != null) ? stats.getSoCot() : 0;
//        long tong = stats != null ? stats.getTongGhe() : 0;

        Long maRap = (p.getRap() != null) ? p.getRap().getId() : null;

        return new PhongDto(
                p.getId(),
                maRap,
                p.getTenPhong(),
                p.getLoaiPhong(),
                p.getTrangThai(),
                p.getCot(),
                p.getHang(),
//                0L,
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    public List<PhongDto> danhSachPhongTrongRap(Long maRap) {
        return phongRepo.findByRap_IdOrderByTenPhongAsc(maRap)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public PhongDto chiTietPhong(Long maPhong) {
        Phong p = phongRepo.findById(maPhong)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu " + maPhong));
        return mapToDto(p);
    }

    @Transactional
    public PhongDto taoPhong(Long maRap, TaoPhongReq req) {
        // Lấy entity Rap để gắn ManyToOne
        Rap rap = rapRepo.findById(maRap)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy rạp " + maRap));

        // 1) Tạo phòng
        Phong phong = new Phong();
        phong.setRap(rap); // 👈 dùng quan hệ
        phong.setTenPhong(req.tenPhong().trim());
        phong.setLoaiPhong(req.loaiPhong().trim());
        phong.setTrangThai(true);

        Phong saved = phongRepo.save(phong);

        // 2) Sinh ghế mặc định
        List<Ghe> gheList = new ArrayList<>();
        for (int row = 1; row <= req.hang(); row++) {
            char rowChar = (char) ('A' + row - 1);
            String hang = String.valueOf(rowChar);
            for (int col = 1; col <= req.cot(); col++) {
                String label = String.format("%s%02d", hang, col);

                Ghe ghe = Ghe.builder()
                        .phong(saved)          // 👈 gắn phòng, KHÔNG dùng maPhong
                        .hang(hang)
                        .cot(col)
                        .nhanGhe(label)
                        .loaiGhe("NORMAL")
                        .nhomCouple(null)
                        .hoatDong(true)
                        .heSoGia(BigDecimal.ONE)
                        .build();

                gheList.add(ghe);
            }
        }
        gheRepo.saveAll(gheList);

        return mapToDto(saved);
    }


    @Transactional
    public PhongDto capNhatPhong(Long maPhong, TaoPhongReq req) {
        Phong p = phongRepo.findById(maPhong)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu " + maPhong));

        p.setTenPhong(req.tenPhong().trim());
        p.setLoaiPhong(req.loaiPhong().trim());
        p.setHang(req.hang());
        p.setCot(req.cot());
        // không đụng tới rap ở đây

        Phong saved = phongRepo.save(p);
        return mapToDto(saved);
    }

    @Transactional
    public void xoaPhong(Long maPhong) {
        // Xóa ghế trước theo quan hệ
        gheRepo.deleteByPhong_Id(maPhong);
        phongRepo.deleteById(maPhong);
    }


    public List<GheDto> danhSachGhe(Long maPhong) {
        return gheRepo.findByPhong_IdOrderByHangAscCotAsc(maPhong)
                .stream()
                .map(g -> {

                    Integer hangIndex = (int) g.getHang().charAt(0) - (int) 'A';

                    return new GheDto(
                            g.getId(),
                            g.getNhanGhe(),
                            hangIndex,
                            g.getCot(),
                            g.getLoaiGhe(),
                            g.getNhomCouple(),
                            g.getCoupleRole()
                    );
                })
                .toList();
    }

    @Transactional
    public void capNhatCauHinhGhe(Long maPhong, List<GheUpdateReq> dsGhe) {
        Map<Long, GheUpdateReq> map = dsGhe.stream()
                .collect(Collectors.toMap(GheUpdateReq::maGhe, it -> it));

        List<Ghe> gheList = gheRepo.findByPhong_IdOrderByHangAscCotAsc(maPhong);

        for (Ghe ghe : gheList) {
            GheUpdateReq req = map.get(ghe.getId());
            if (req == null) continue;

            if (req.loaiGhe() != null) ghe.setLoaiGhe(req.loaiGhe());
            if (req.hoatDong() != null) ghe.setHoatDong(req.hoatDong());
            ghe.setNhomCouple(req.nhomCouple());
        }
        // JPA tự flush
    }
}

