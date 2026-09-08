package vn.edu.epu.quanlyhoso.auth.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "accounts")

public class Account implements UserDetails {
    /**
	 * 
	 */	
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(name = "is_active")
    private Boolean isActive = true;
    //Quan hệ 1-N
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 1. Nếu tài khoản chưa được gán Role, trả về danh sách rỗng để tránh lỗi NullPointer
        if (this.role == null || this.role.getRoleName() == null) {
            return List.of(); 
        }
        // 2. Lấy cái chữ "ADMIN" từ DB, nhét vào cái bao bì SimpleGrantedAuthority để Spring nó hiểu
        return List.of(new SimpleGrantedAuthority(this.role.getRoleName()));
    }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { 
        // Lấy chính cái trạng thái trong Database ra để cho Spring Security kiểm tra
        return this.isActive != null ? this.isActive : false; 
    }
}
