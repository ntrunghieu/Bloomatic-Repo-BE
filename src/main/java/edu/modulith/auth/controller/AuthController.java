package edu.modulith.auth.controller;
import edu.modulith.auth.domain.TaiKhoan;
import edu.modulith.auth.domain.TaiKhoanRepo;
import edu.modulith.auth.dto.LoginReq;
import edu.modulith.auth.dto.LoginRes;
import edu.modulith.auth.service.AuthUserDetailsService;
import edu.modulith.auth.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TaiKhoanRepo taiKhoanRepo;
    private final PasswordEncoder encoder;
    private final AuthUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public LoginRes login(@Valid @RequestBody LoginReq req) {
        // Tìm user theo email
        TaiKhoan tk = taiKhoanRepo.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // Check password
        if (!encoder.matches(req.password(), tk.getMatKhau())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

        // Lấy UserDetails (khai báo authorities trong CustomUserDetailsService)
        UserDetails ud = userDetailsService.loadUserByUsername(tk.getEmail());

        // Lấy set quyền để trả về cho FE
        Set<String> roles = ud.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // Sinh JWT
        String token = jwtService.generateToken(ud);

        // Trả LoginRes đúng role trong DB
        return new LoginRes(
                token,
                tk.getMaTaiKhoan(),
                tk.getHoTen(),
                tk.getEmail(),
                roles
        );
    }


    // (Tuỳ chọn) Đăng ký nhanh
    // @PostMapping("/register")
    // public String register(@Valid @RequestBody RegisterReq req) { ... }
}

