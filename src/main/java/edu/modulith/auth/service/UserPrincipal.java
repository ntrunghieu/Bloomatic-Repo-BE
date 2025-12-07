package edu.modulith.auth.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

// Giả định đây là lớp UserPrincipal của bạn
public class UserPrincipal implements UserDetails {
    private Long id; // <--- Cần có trường này
    private String username;
    private String password;
    // ... các trường khác

    // Constructor...
    public UserPrincipal(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        // ...
    }

    // THÊM GETTER CHO ID
    public Long getId() { // <--- Phương thức này phải tồn tại
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    // ... các phương thức khác của UserDetails (getUsername, getPassword, etc.)
}
