package edu.modulith.datve.domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDatVeRepo extends JpaRepository<ChiTietDatVe, Long> {
    @Query(value = "SELECT * FROM chi_tiet_dat_ve c " +
                    "WHERE c.ma_suat_chieu = :maLichChieu " +
                    "AND c.ma_ghe IN :dsMaGhe", nativeQuery = true )
    List<ChiTietDatVe> findTrungGhe(@Param("maLichChieu") Long maLichChieu, @Param("dsMaGhe") List<Long> dsMaGhe);

    @Query("""
        select ctdv.ghe.id
        from ChiTietDatVe ctdv
        join ctdv.datVe dv
        where ctdv.lichChieu.maLichChieu = :lichChieuId
          and dv.trangThai in ('PENDING', 'PAID')
    """)
    List<Long> findBookedSeatIdsByLichChieu(@Param("lichChieuId") Long lichChieuId);

}
