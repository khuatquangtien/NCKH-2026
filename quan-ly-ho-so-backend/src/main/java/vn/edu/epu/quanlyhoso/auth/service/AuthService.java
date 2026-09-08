package vn.edu.epu.quanlyhoso.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder; // MỚI THÊM
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.edu.epu.quanlyhoso.auth.dto.request.AuthRequest;
import vn.edu.epu.quanlyhoso.auth.dto.request.RegisterRequest;
import vn.edu.epu.quanlyhoso.auth.dto.response.AuthResponse;
import vn.edu.epu.quanlyhoso.auth.entity.Account;
import vn.edu.epu.quanlyhoso.auth.entity.Role;
import vn.edu.epu.quanlyhoso.auth.repository.AccountRepository;
import vn.edu.epu.quanlyhoso.auth.repository.RoleRepository;





@Service
@RequiredArgsConstructor
public class AuthService {
    // PHẦN 1: CÁC BIẾN KHAI BÁO 
    private final AccountRepository repository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    
    // Hai thanh gươm mới dành cho tính năng Đăng Ký
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // PHẦN 2: HÀM LOGIN 
    public AuthResponse authenticate(AuthRequest request) {
        // 1. Quăng vào máy xác thực. Sai mật khẩu là nó văng Exception ngay chỗ này
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Mật khẩu đúng rồi thì lôi tài khoản từ DB lên
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow();

        // 3. Móc cái quyền (ROLE) ra nhét vào Extra Claims
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().getRoleName());

        // 4. In vé (Token)
        var jwtToken = jwtService.generateToken(extraClaims, user);

        // 5. Đóng gói lại gửi về cho Controller
        return new AuthResponse(jwtToken, user.getRole().getRoleName());
    }

    
    // PHẦN 3: HÀM REGISTER 
    public AuthResponse register(RegisterRequest request) {
        // 1. Check xem Username đã bị thằng nào lấy mất chưa
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại trong hệ thống!");
        }

        // 2. Gán cứng quyền LECTURER (ID = 2) cho toàn bộ user mới
        Role role = roleRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền (Role) này trong DB!")); 

        // 3. Khởi tạo đối tượng Account mới
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setEmail(request.getEmail());
        
        // 4. Băm nát mật khẩu
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        
        account.setRole(role);
        account.setIsActive(true);

        // 5. Ném vào két sắt
        repository.save(account);

        // 6. Đóng gói Extra Claims
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", role.getRoleName());

        // 7. In thẻ Token
        var jwtToken = jwtService.generateToken(extraClaims, account);
        
        return new AuthResponse(jwtToken, role.getRoleName());
    }
}