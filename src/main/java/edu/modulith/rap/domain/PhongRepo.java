package edu.modulith.rap.domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhongRepo extends JpaRepository<Phong, Long> {
    @Query(
            value = "SELECT * FROM ghe g " +
                    "WHERE g.ma_phong = :maPhong " +
                    "ORDER BY g.hang, g.cot",
            nativeQuery = true
    )
    List<Ghe> findByMaPhongOrderByHangCot(@Param("maPhong") Long maPhong);

    @Query(value = "SELECT * FROM phong WHERE ma_rap = :maRap ORDER BY ten_phong ASC", nativeQuery = true)
    List<Phong> findByMaRap(@Param("maRap") Long maRap);

    List<Phong> findByRap_IdOrderByTenPhongAsc(Long maRap);

    @Query(value = "SELECT * FROM phong WHERE ma_phong = :maPhong", nativeQuery = true)
    Optional<Phong> findById1(@Param("maPhong") Long maPhong);
}
