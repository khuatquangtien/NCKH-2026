//package vn.edu.epu.quanlyhoso.common.config;
//
//import java.io.IOException;
//import java.util.Collections;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//public class HeaderAuthenticationFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        
//        // Lấy role từ header
//        String userRole = request.getHeader("X-User-Role");
//        
//        System.out.println("🔍 Header X-User-Role: " + userRole);
//
//        if (userRole != null && !userRole.isEmpty()) {
//            // Tạo authentication object với role từ header
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(
//                            userRole,
//                            null,
//                            Collections.singletonList(new SimpleGrantedAuthority(userRole))
//                    );
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            System.out.println("✅ Đã set authentication với role: " + userRole);
//        } else {
//            System.out.println("⚠️ Không tìm thấy header X-User-Role");
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}