package edu.modulith.lichchieu.domain;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LichChieuRepo extends JpaRepository<LichChieu, Long> {
    // Lịch chiếu theo phim + ngày + rạp (optional)
    @Query(
            value = "SELECT lc.* FROM lich_chieu lc " +
                    "JOIN phim p   ON lc.ma_phim = p.ma_phim " +
                    "JOIN phong ph ON lc.ma_phong = ph.ma_phong " +
                    "JOIN rap r    ON ph.ma_rap = r.ma_rap " +
                    "WHERE (:maPhim IS NULL OR lc.ma_phim = :maPhim) " +
                    "AND (:maRap IS NULL OR r.ma_rap = :maRap) " +
                    "AND DATE(lc.gio_bat_dau) = :ngay " +
                    "ORDER BY lc.gio_bat_dau",
            nativeQuery = true
    )
    List<LichChieu> timLichChieu(
            @Param("maPhim") Long maPhim,
            @Param("maRap") Long maRap,
            @Param("ngay") LocalDate ngay
    );

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO lich_chieu (ma_phim, ngay_bat_dau, ngay_ket_thuc) VALUES (:maPhim, :startDate, :endDate)",
            nativeQuery = true)
    int insertShowtimeOnlyDates(@Param("maPhim") Long maPhim,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);

//    @Query("SELECT lc FROM LichChieu lc WHERE " +
//            "(:maRapChieu IS NULL OR lc.phong.rap.id = :maRapChieu) AND " +
//            "(:maPhong IS NULL OR lc.phong.id = :maPhong) AND " +
//            "(:ngayChieu IS NULL OR lc.ngayBatDau = :ngayChieu)")
//    List<LichChieu> findFilteredSuatChieu(Long maRapChieu, Long maPhong, LocalDate ngayChieu);

    @Query(value = "SELECT lc.* " +
            "FROM lich_chieu lc " +
            // JOIN: Kết nối LichChieu với Phong qua ma_phong
            "INNER JOIN phong p ON lc.ma_phong = p.ma_phong " +
            // JOIN: Kết nối Phong với Rap qua ma_rap
            "INNER JOIN rap r ON p.ma_rap = r.ma_rap " +
            "WHERE " +
            // Lọc theo Mã Rạp (maRapChieu)
            "(:maRapChieu IS NULL OR r.ma_rap = :maRapChieu) " +
            // Lọc theo Mã Phòng (maPhong)
            "AND (:maPhong IS NULL OR p.ma_phong = :maPhong) " +
            // Lọc theo Ngày Chiếu (ngayChieu)
            "AND (:ngayChieu IS NULL OR lc.ngay_bat_dau = :ngayChieu)",
            nativeQuery = true)
    List<LichChieu> findFilteredSuatChieu(
            @Param("maRapChieu") Long maRapChieu,
            @Param("maPhong") Long maPhong,
            @Param("ngayChieu") LocalDate ngayChieu
    );

    @Modifying
    @Query(value = "UPDATE lich_chieu " +
            "SET ma_phim = :maPhim, " +
            "ma_phong = :maPhong, " +
            "ngay_bat_dau = :ngayBatDau, " +
            "ngay_ket_thuc = :ngayKetThuc, " +
            "dinh_dang = :dinhDang, " +
            "hinh_thuc_dich = :hinhThucDich, " +
            "gio_bat_dau = :gioBatDau, " +
            "gio_ket_thuc = :gioKetThuc, " +
            "trang_thai = :trangThai, " +
            "gia_co_so = :giaCoSo " +
            "WHERE ma_lich_chieu = :id", // <--- Lọc theo ID suất chiếu
            nativeQuery = true)
    int updateSlotNative(
            @Param("id") Long id,
            @Param("maPhim") Long maPhim,
            @Param("maPhong") Long maPhong,
            @Param("ngayBatDau") LocalDate ngayBatDau,
            @Param("ngayKetThuc") LocalDate ngayKetThuc,
            @Param("dinhDang") String dinhDang,
            @Param("hinhThucDich") String hinhThucDich,
            @Param("gioBatDau") LocalDateTime gioBatDau,
            @Param("gioKetThuc") LocalDateTime gioKetThuc,
            @Param("trangThai") String trangThai,
            @Param("giaCoSo") BigDecimal giaCoSo
    );


}
