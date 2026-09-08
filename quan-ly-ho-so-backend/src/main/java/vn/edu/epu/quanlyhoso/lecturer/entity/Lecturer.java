package vn.edu.epu.quanlyhoso.lecturer.entity;

import jakarta.persistence.*;
import lombok.Data;
import vn.edu.epu.quanlyhoso.auth.entity.Account;

@Data
@Entity
@Table(name = "lecturers")
public class Lecturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Quan hệ 1-1 với Account
    @OneToOne
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "academic_rank") // Học hàm (Thay vì academic_title)
    private String academicRank;

    @Column(name = "academic_degree") // Học vị (Thay vì degree)
    private String academicDegree;

    @Column(name = "faculty", nullable = false) // Khoa (Thay vì department)
    private String faculty;

    @Column(name = "research_direction") // Hướng nghiên cứu
    private String researchDirection;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private java.sql.Timestamp updatedAt;
}
