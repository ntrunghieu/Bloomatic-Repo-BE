package edu.modulith.theloaiphim.dto;

import edu.modulith.phim.domain.Phim;
import edu.modulith.theloaiphim.domain.PhimTheLoai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhimTheLoaiRepository extends JpaRepository<PhimTheLoai, Long> {

        List<PhimTheLoai> findByPhim(Phim phim);

        void deleteByPhim_Id(Long phimId);

        List<PhimTheLoai> findByPhim_Id(Long phimId);

        @Query("SELECT ptl FROM PhimTheLoai ptl JOIN FETCH ptl.theLoai WHERE ptl.phim.id IN :phimIds")
        List<PhimTheLoai> findByPhim_IdInWithTheLoai(@Param("phimIds") List<Long> phimIds);
}
