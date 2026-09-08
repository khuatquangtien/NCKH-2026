package vn.edu.epu.quanlyhoso.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
//Tạo sẵn bảng Role để java nhận biết dưới database có những gì
@Data
@Entity
@Table(name = "roles")
public class Role {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;
}