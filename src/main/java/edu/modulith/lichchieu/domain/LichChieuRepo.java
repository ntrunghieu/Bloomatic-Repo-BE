package edu.modulith.lichchieu.domain;
import edu.modulith.phim.domain.Phim;
import edu.modulith.phim.dto.PhimDto;
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
            "ngay_chieu = :ngayChieu, " +
//            "ngay_ket_thuc = :ngayKetThuc, " +
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
            @Param("ngayChieu") LocalDate ngayChieu,
//            @Param("ngayKetThuc") LocalDate ngayKetThuc,
            @Param("dinhDang") String dinhDang,
            @Param("hinhThucDich") String hinhThucDich,
            @Param("gioBatDau") LocalDateTime gioBatDau,
            @Param("gioKetThuc") LocalDateTime gioKetThuc,
            @Param("trangThai") String trangThai,
            @Param("giaCoSo") BigDecimal giaCoSo
    );

    // Truy vấn tất cả lịch chiếu phù hợp với điều kiện lọc: Ngày và Tên Rạp
    @Query("SELECT lc FROM LichChieu lc " +
            "JOIN lc.phong p " +    // Liên kết LichChieu -> Phong
            "JOIN p.rap r " +       // Liên kết Phong -> Rap
            "WHERE lc.ngayBatDau = :ngayChieu AND lc.trangThai = 'ACTIVE' " +
            // Điều kiện lọc theo tên rạp: kiểm tra null/rỗng HOẶC tìm kiếm một phần tên
            "AND (:tenRap IS NULL OR :tenRap = '' OR r.tenRap LIKE %:tenRap%) " +
            "ORDER BY r.tenRap, lc.gioBatDau")
    List<LichChieu> findSchedulesFiltered(
            @Param("ngayChieu") LocalDate ngayChieu,
            @Param("tenRap") String tenRap // Tham số mới
    );

    // Sử dụng @Query để định nghĩa truy vấn kiểm tra trùng lặp
    @Query("SELECT CASE WHEN COUNT(lc) > 0 THEN TRUE ELSE FALSE END FROM LichChieu lc " +
            "WHERE lc.phong.id = :maPhong AND " +
            "(:newStart < lc.gioKetThuc AND lc.gioBatDau < :newEnd)")
    boolean existsOverlappingSlot(
            @Param("maPhong") Long maPhong,
            @Param("newStart") LocalDateTime newStart,
            @Param("newEnd") LocalDateTime newEnd
    );

    @Query("""
    SELECT CASE WHEN COUNT(lc) > 0 THEN TRUE ELSE FALSE END 
    FROM LichChieu lc 
    WHERE lc.phong.id = :phongId 
    AND lc.id != :excludedSlotId 
    AND (
        (:newGioBatDau < lc.gioKetThuc AND :newGioKetThuc > lc.gioBatDau)
    )
""")
    boolean existsOverlappingSlotExcludingSelf(
            @Param("phongId") Long phongId,
            @Param("newGioBatDau") LocalDateTime newGioBatDau,
            @Param("newGioKetThuc") LocalDateTime newGioKetThuc,
            @Param("excludedSlotId") Long excludedSlotId
    );

    @Query("""
    SELECT DISTINCT lc FROM LichChieu lc
    JOIN FETCH lc.phim p
    LEFT JOIN FETCH p.phimTheLoaiSet ptls
    LEFT JOIN FETCH ptls.theLoai tl
    JOIN FETCH lc.phong phong
    JOIN FETCH phong.rap r
    WHERE lc.ngayChieu BETWEEN :startDate AND :endDate
      AND LOWER(r.diaChi) LIKE LOWER(CONCAT('%', :city, '%'))
""")
    List<LichChieu> findSchedulesByDateRangeAndCityWithDetails(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("city") String city);

    @Query("SELECT DISTINCT lc.phim.id FROM LichChieu lc")
    List<LichChieu> findDistinctMoviesScheduled();

    @Query("SELECT lc FROM LichChieu lc WHERE lc.ngayChieu IS NULL")
    List<LichChieu> findScheduledShowtimesWithoutSpecificDate();


}
