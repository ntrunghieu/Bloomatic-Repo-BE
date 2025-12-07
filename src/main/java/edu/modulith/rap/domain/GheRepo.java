package edu.modulith.rap.domain;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GheRepo extends JpaRepository<Ghe, Long> {

    // Lấy danh sách ghế theo phòng
    List<Ghe> findByPhong_IdOrderByHangAscCotAsc(Long phongId);


    @Modifying
    @Transactional
    void deleteByPhong_Id(Long phongId);

    // Dùng để tính thống kê số hàng, số cột, tổng ghế cho 1 phòng
    interface PhongSeatStats {
        Integer getSoHang();
        Integer getSoCot();
//        Integer getTongGhe();
    }

    @Query("""
           select count(distinct g.hang) as soHang,
                  max(g.cot)             as soCot,
                  count(g)               as tongGhe
           from Ghe g
           where g.phong.id = :maPhong
           """)
    PhongSeatStats tinhThongKePhong(@Param("maPhong") Long maPhong);

    @Modifying
    @Transactional
    void deleteAllByPhong_Id(Long maPhong);
    int countByPhong_Id(Long maPhong);
    List<Ghe> findByPhong_Id(Long phongId);

    List<Ghe> findByPhong_IdAndNhanGheIn(Long phongId, List<String> nhanGheList);


}
