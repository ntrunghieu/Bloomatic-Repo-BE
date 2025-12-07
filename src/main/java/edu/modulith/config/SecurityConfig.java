package edu.modulith.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/dev/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/phim/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/rap/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ve/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/dat-ve/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/chi-tiet-dat-ve/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/lich-chieu/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/thanh-toan/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/the-loai/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/filter/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/public/suat-chieu/{lichChieuId}/ghe/stream").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/public/suat-chieu/**").hasAuthority("ROLE_CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/lich-chieu/dat-ve/**").hasAuthority("ROLE_CUSTOMER")
                        .requestMatchers("/api/suat-chieu/**").permitAll()
                        .requestMatchers("/api/lich-chieu/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/phim/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/rap/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/phong/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/phim/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/actuator/health"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
