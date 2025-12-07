package edu.modulith.auth.service;

import edu.modulith.auth.domain.CustomUserDetails;
import edu.modulith.auth.domain.TaiKhoan;
import edu.modulith.auth.domain.TaiKhoanRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {
    private final TaiKhoanRepo taiKhoanRepo;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TaiKhoan tk = taiKhoanRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản"));

        String authority = tk.getPhanQuyen().getQuyen(); // hoặc tk.getRole() / tk.getLoaiTaiKhoan() tuỳ bạn

        if (!authority.startsWith("ROLE_")) {
            authority = "ROLE_" + authority;
        }

        boolean locked = !"ACTIVE".equalsIgnoreCase(tk.getTrangThai());

        return new CustomUserDetails(
                tk.getMaTaiKhoan(),
                tk.getEmail(),
                tk.getMatKhau(),
                List.of(new SimpleGrantedAuthority(authority)),
                locked
        );
    }
//    @Override
//    @Transactional(readOnly = true)
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        TaiKhoan tk = taiKhoanRepo.findByEmailIgnoreCase(email)
//                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản"));
//
//        // Lấy quyền từ bảng phan_quyen
//        String rawRole = tk.getPhanQuyen() != null
//                ? tk.getPhanQuyen().getQuyen()   // ví dụ: ADMIN
////                : "USER";
//                : "CUSTOMER";
//
//        String authority = rawRole.toUpperCase();
//        if (!authority.startsWith("ROLE_")) {
//            authority = "ROLE_" + authority;   // -> ROLE_ADMIN / ROLE_USER
//        }
//
//        boolean locked = !"ACTIVE".equalsIgnoreCase(tk.getTrangThai());
//
//        return org.springframework.security.core.userdetails.User
//                .withUsername(tk.getEmail())
//                .password(tk.getMatKhau())
//                .authorities(authority)
//                .accountLocked(locked)
//                .build();
//    }
}

