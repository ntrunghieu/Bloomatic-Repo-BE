package edu.modulith.auth.service;

import edu.modulith.auth.context.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final AuthenticationFacade authenticationFacade;

    // Dependency Injection thông qua Constructor (Cách được khuyến nghị)
    @Autowired
    public UserService(AuthenticationFacade authenticationFacade) {
        this.authenticationFacade = authenticationFacade;
    }

    public void processUserAction() {
        // Sử dụng Facade để lấy thông tin
        String username = authenticationFacade.getCurrentUsername();

        if (username != null) {
            System.out.println("Người dùng hiện tại: " + username);
            // ... Logic xử lý dựa trên username
        } else {
            System.out.println("Người dùng chưa được xác thực.");
        }
    }
}
