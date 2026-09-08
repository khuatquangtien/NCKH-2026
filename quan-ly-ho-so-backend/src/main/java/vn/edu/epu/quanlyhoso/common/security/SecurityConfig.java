package vn.edu.epu.quanlyhoso.common.security;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import vn.edu.epu.quanlyhoso.auth.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	.cors(cors -> cors.configurationSource(corsConfigurationSource())) 

            .csrf(csrf -> csrf.disable()) // Tắt bảo vệ CSRF vì xài JWT Token
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/error").permitAll() // ĐẶC BIỆT QUAN TRỌNG: Thả cửa cho mọi API bắt đầu bằng /api/auth (Login)
                // 2. Khu vực Test:
                // Chỉ dành cho người có Role là ADMIN
                .requestMatchers("/api/test/admin").hasAuthority("ADMIN")
                // Chỉ có admin mới là người thay đổi được quyền
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN") 
                .requestMatchers("/api/profile/me").permitAll() // T
                // Dành cho bất cứ ai đã đăng nhập (Có Token)
                .requestMatchers("/api/test/user").authenticated()
                .requestMatchers("/api/accounts/**").hasAnyAuthority("ADMIN")
                .anyRequest().authenticated() // TẤT CẢ các API còn lại bắt buộc phải có Token hợp lệ mới cho vào
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không lưu phiên (Session) trên Server
            .authenticationProvider(authenticationProvider)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);// Xếp ông bảo vệ JWT lên đầu hàng;

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Cho phép TẤT CẢ các nguồn (Origin) truy cập
        configuration.setAllowedOriginPatterns(Collections.singletonList("http://localhost:[*]")); 
        
        // Cho phép tất cả các phương thức HTTP (GET, POST, PUT, DELETE, v.v.)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); 
        
        // Cho phép gửi kèm tất cả các loại Header (bao gồm cả header "Authorization" chứa JWT)
        configuration.setAllowedHeaders(Collections.singletonList("*")); 
        
        // Cho phép trình duyệt gửi Cookie hoặc thông tin xác thực kèm theo (rất cần cho JWT/Session)
        configuration.setAllowCredentials(true); 
        
        // Áp dụng cấu hình này cho TOÀN BỘ các API trong hệ thống
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
