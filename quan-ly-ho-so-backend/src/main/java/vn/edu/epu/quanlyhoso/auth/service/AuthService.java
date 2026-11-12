package vn.edu.epu.quanlyhoso.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder; // MỚI THÊM
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.edu.epu.quanlyhoso.auth.dto.request.AuthRequest;
import vn.edu.epu.quanlyhoso.auth.dto.request.RegisterRequest;
import vn.edu.epu.quanlyhoso.auth.dto.response.AuthResponse;
import vn.edu.epu.quanlyhoso.auth.entity.Account;
import vn.edu.epu.quanlyhoso.auth.entity.Role;
import vn.edu.epu.quanlyhoso.auth.repository.AccountRepository;
import vn.edu.epu.quanlyhoso.auth.repository.RoleRepository;
import vn.edu.epu.quanlyhoso.common.exception.BusinessException;
import vn.edu.epu.quanlyhoso.auth.dto.response.CurrentUserResponse;
import vn.edu.epu.quanlyhoso.lecturer.entity.Lecturer;
import vn.edu.epu.quanlyhoso.lecturer.repository.LecturerRepository;

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
    private final LecturerRepository lecturerRepository;

    public CurrentUserResponse getCurrentUser(String username) {
        Account account = repository.findByUsername(username).orElseThrow();
        Lecturer lecturer = lecturerRepository.findByAccountId(account.getId()).orElse(null);
        return new CurrentUserResponse(account.getId(), account.getUsername(), account.getEmail(),
                account.getRole().getRoleName(), lecturer == null ? null : lecturer.getId(),
                lecturer == null ? null : lecturer.getFullName(), lecturer == null ? null : lecturer.getFaculty());
    }

    // PHẦN 2: HÀM LOGIN
    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        var user = repository.findByUsername(request.getUsername())
                .orElseThrow();

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().getRoleName());

        var jwtToken = jwtService.generateToken(extraClaims, user);

        return new AuthResponse(jwtToken, user.getRole().getRoleName());
    }

    // PHẦN 3: HÀM REGISTER
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("Tên đăng nhập đã tồn tại trong hệ thống!");
        }

        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Email đã được sử dụng!");
        }
        Role role = roleRepository.findByRoleName("LECTURER")
                .orElseThrow(() -> new IllegalStateException("Not found role LECTURER"));

        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setEmail(request.getEmail());

        account.setPassword(passwordEncoder.encode(request.getPassword()));

        account.setRole(role);
        account.setIsActive(true);

        repository.save(account);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", role.getRoleName());

        var jwtToken = jwtService.generateToken(extraClaims, account);

        return new AuthResponse(jwtToken, role.getRoleName());
    }
}
