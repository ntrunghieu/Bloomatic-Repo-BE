package edu.modulith.auth.context;

import org.springframework.security.core.Authentication;

public interface AuthenticationFacade {
    /** Lấy thông tin xác thực hiện tại */
    Authentication getAuthentication();

    Long getCurrentUserId();

    /** Lấy tên người dùng hiện tại */
    String getCurrentUsername();

    /** Kiểm tra xem người dùng có được xác thực hay không */
    boolean isAuthenticated();
}