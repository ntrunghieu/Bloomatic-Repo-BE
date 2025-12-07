package edu.modulith.auth.context;

//import com.sun.security.auth.UserPrincipal;
import edu.modulith.auth.domain.CustomUserDetails;
import edu.modulith.auth.service.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component // Đánh dấu đây là một Spring Bean
public class AuthenticationFacadeImpl implements AuthenticationFacade {

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = getAuthentication();


        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        // Khi AuthUserDetailsService đã trả về CustomUserDetails
        if (principal instanceof CustomUserDetails cud) {
            return cud.getId();
        }

        // nếu vì lý do gì đó principal là kiểu khác (String username, v.v.)
        return null;
    }

    @Override
    public String getCurrentUsername() {

        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getName();
    }



    @Override
    public boolean isAuthenticated() {
        Authentication authentication = getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }
}
