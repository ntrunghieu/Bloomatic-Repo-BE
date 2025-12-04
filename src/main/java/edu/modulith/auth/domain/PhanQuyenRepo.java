package edu.modulith.auth.domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhanQuyenRepo extends JpaRepository<PhanQuyen, Long> {
}
