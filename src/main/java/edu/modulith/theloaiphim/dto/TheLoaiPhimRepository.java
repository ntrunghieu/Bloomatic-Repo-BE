package edu.modulith.theloaiphim.dto;

import edu.modulith.theloaiphim.domain.TheLoaiPhim;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheLoaiPhimRepository extends JpaRepository<TheLoaiPhim, Long> {

    @Query(
            value = "SELECT tl.ten_the_loai " +
                    "FROM the_loai_phim tl " +
                    "JOIN phim_the_loai ptl ON tl.ma_the_loai = ptl.theloai_id " +
                    "WHERE ptl.movie_id = :maPhim",
            nativeQuery = true
    )
    @Transactional
    List<String> findTenTheLoaiByMaPhim(@Param("maPhim") Long maPhim);

    @Query(
            value = "SELECT " +
                    "tl.ma_the_loai AS maTheLoai, " + // Lấy cột ma_the_loai, đặt alias là maTheLoai
                    "tl.ten_the_loai AS tenTheLoai " + // Lấy cột ten_the_loai, đặt alias là tenTheLoai
                    "FROM the_loai_phim tl",
            nativeQuery = true
    )
    List<TheLoaiDto> getAll();

}

