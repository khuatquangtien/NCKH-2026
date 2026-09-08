package vn.edu.epu.quanlyhoso.project.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import vn.edu.epu.quanlyhoso.lecturer.entity.Lecturer;
import vn.edu.epu.quanlyhoso.project.enums.ProjectStatus;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String objective;

    @Column(name = "expected_product", columnDefinition = "TEXT")
    private String expectedProduct;

    @Column(name = "estimated_budget", nullable = false, precision = 15, scale = 2)
    private BigDecimal estimatedBudget;

    @Column(name = "approved_budget", precision = 15, scale = 2)
    private BigDecimal approvedBudget;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ProjectStatus status;

    @Column(nullable = false, length = 100)
    private String faculty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private Lecturer leader;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "project")
    private List<ProjectReport> reports = new ArrayList<>();

    @OneToOne(mappedBy = "project", fetch = FetchType.LAZY)
    private ProjectSubmission submission;

    @OneToMany(mappedBy = "project")
    private List<ProjectMember> members = new ArrayList<>();

    public Project() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getExpectedProduct() {
        return expectedProduct;
    }

    public void setExpectedProduct(String expectedProduct) {
        this.expectedProduct = expectedProduct;
    }

    public BigDecimal getEstimatedBudget() {
        return estimatedBudget;
    }

    public void setEstimatedBudget(BigDecimal estimatedBudget) {
        this.estimatedBudget = estimatedBudget;
    }

    public BigDecimal getApprovedBudget() {
        return approvedBudget;
    }

    public void setApprovedBudget(BigDecimal approvedBudget) {
        this.approvedBudget = approvedBudget;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public Lecturer getLeader() {
        return leader;
    }

    public void setLeader(Lecturer leader) {
        this.leader = leader;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ProjectReport> getReports() {
        return reports;
    }

    public void setReports(List<ProjectReport> reports) {
        this.reports = reports;
    }

    public ProjectSubmission getSubmission() {
        return submission;
    }

    public void setSubmission(ProjectSubmission submission) {
        this.submission = submission;
    }

    public List<ProjectMember> getMembers() {
        return members;
    }

    public void setMembers(List<ProjectMember> members) {
        this.members = members;
    }
}
