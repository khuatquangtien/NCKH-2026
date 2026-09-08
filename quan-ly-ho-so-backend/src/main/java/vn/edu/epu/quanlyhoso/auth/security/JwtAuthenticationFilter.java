package vn.edu.epu.quanlyhoso.auth.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import vn.edu.epu.quanlyhoso.auth.service.JwtService;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String username = null; 

        // 1. Kiểm tra xem có mang thẻ không (có Header Bearer không)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Tách lấy lõi Token
        jwt = authHeader.substring(7);

        // 3. Giải mã Token (Bọc try-catch để chặn lỗi văng ra làm sập luồng)
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            // Nhận Token không đúng hoặc hết hạn -> username vẫn là null -> Tự động bị Spring từ chối ở cửa sau
        }

        // 4. Nếu có tên người dùng và chưa được cấp quyền trong phiên này
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 5. Kiểm tra tính hợp lệ của Token so với DB
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Cấp quyền thành công!
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } 
        }
        
        // Chuyển cho màng lọc tiếp theo xử lý
        filterChain.doFilter(request, response);
    }
}