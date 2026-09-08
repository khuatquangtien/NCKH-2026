package vn.edu.epu.quanlyhoso.auth.dto.response;

public class AuthResponse {
    private String token;
    private String role;

    // Tự tạo Constructor chuẩn
    public AuthResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
