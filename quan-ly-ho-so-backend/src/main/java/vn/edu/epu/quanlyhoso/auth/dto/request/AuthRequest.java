package vn.edu.epu.quanlyhoso.auth.dto.request;

public class AuthRequest {
    private String username;
    private String password;
    private String keyString;
    // Tự viết Constructor & Getter để khỏi cần Lombok
    public AuthRequest() {}
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
