package edu.modulith.auth.domain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaiKhoanRepo extends JpaRepository<TaiKhoan, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<TaiKhoan> findByEmailIgnoreCase(String email);
}
