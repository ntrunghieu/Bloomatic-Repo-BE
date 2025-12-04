package edu.modulith.config;

import edu.modulith.auth.domain.TaiKhoan;
import edu.modulith.auth.domain.TaiKhoanRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class DevDataSeeder {

    @Bean
    CommandLineRunner seedTestUser(TaiKhoanRepo repo, PasswordEncoder encoder) {
        return args -> {
            final String email = "c";
            if (!repo.existsByEmailIgnoreCase(email)) {
                TaiKhoan u = TaiKhoan.builder()
                        .hoTen("Quản trị viên")
                        .ngaySinh(LocalDate.of(2000, 1, 1))
                        .email(email)
                        .diaChi("Da Nang")
                        .soDienThoai("0900000000")
                        .matKhau(encoder.encode("123456")) // băm BCrypt
                        .trangThai("ACTIVE")
                        .build();
                repo.save(u);
                System.out.println("[SEED] Đã tạo tài khoản test: " + email + " / 123456");
            } else {
                System.out.println("[SEED] Tài khoản đã tồn tại: " + email);
            }
        };
    }
}

