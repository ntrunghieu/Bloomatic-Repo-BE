package edu.modulith.phim.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface PhimRepo extends JpaRepository<Phim, Long> {
    Page<Phim> findByTrangThai(String trangThai, Pageable pageable);

    // Tìm phim theo trạng thái (null => tất cả)
    @Query(
            value = "SELECT * FROM phim p " +
                    "WHERE (:trangThai IS NULL OR p.trang_thai = :trangThai) " +
                    "ORDER BY p.ngay_khoi_chieu DESC, p.ten_phim ASC",
            countQuery = "SELECT COUNT(*) FROM phim p " +
                    "WHERE (:trangThai IS NULL OR p.trang_thai = :trangThai)",
            nativeQuery = true
    )
    Page<Phim> timKiemPhim(
            @Param("trangThai") String trangThai,
            Pageable pageable
    );

    // Phim đang chiếu trong khoảng ngày
    @Query(
            value = "SELECT DISTINCT p.* FROM phim p " +
                    "JOIN lich_chieu lc ON lc.ma_phim = p.ma_phim " +
                    "WHERE p.trang_thai = 'Đang chiếu' " +
                    "AND DATE(lc.gio_bat_dau) = :ngay " +
                    "ORDER BY lc.gio_bat_dau",
            nativeQuery = true
    )
    List<Phim> findNowShowingByNgay(@Param("ngay") LocalDate ngay);

    List<Phim> findByNgayKhoiChieuGreaterThanEqual(LocalDate date);

    @Query("SELECT DISTINCT p FROM LichChieu lc JOIN lc.phim p WHERE " +
            "p.trangThai = :trangThai AND " +
            "lc.ngayBatDau <= :currentDate AND " +
            "lc.ngayKetThuc >= :currentDate")
    List<Phim> findActiveAndScheduledMovies(
            @Param("trangThai") String trangThai,
            @Param("currentDate") LocalDate currentDate
    );

    List<Phim> findByTrangThai(String trangThai);
    List<Phim> findByTrangThaiIn(Collection<String> trangThais);




}
