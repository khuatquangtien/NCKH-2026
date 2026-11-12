package vn.edu.epu.quanlyhoso.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.edu.epu.quanlyhoso.auth.dto.request.AuthRequest;
import vn.edu.epu.quanlyhoso.auth.dto.request.RegisterRequest;
import vn.edu.epu.quanlyhoso.auth.dto.response.AuthResponse;
import vn.edu.epu.quanlyhoso.auth.dto.response.CurrentUserResponse;
import vn.edu.epu.quanlyhoso.auth.service.AuthService;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getUser(Authentication authentication) {
        return ResponseEntity.ok(authService.getCurrentUser(authentication.getName()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/register")
    // Thêm @Valid vào đây để chặn cổng ngay từ đầu!
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
