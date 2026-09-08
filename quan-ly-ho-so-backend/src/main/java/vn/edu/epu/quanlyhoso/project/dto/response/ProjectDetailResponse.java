package vn.edu.epu.quanlyhoso.project.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import vn.edu.epu.quanlyhoso.project.enums.ProjectStatus;

public class ProjectDetailResponse {

    private Integer id;

    private String title;

    private String objective;

    private String expectedProduct;

    private BigDecimal estimatedBudget;

    private BigDecimal approvedBudget;

    private ProjectStatus status;

    private String faculty;

    private Integer leaderId;

    private String leaderName;

    private LocalDateTime createdAt;

    private List<ProjectMemberResponse> members;

    private List<ProjectReportResponse> reports;

    private ProjectSubmissionResponse submission;

    public ProjectDetailResponse() {
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

    public Integer getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Integer leaderId) {
        this.leaderId = leaderId;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ProjectMemberResponse> getMembers() {
        return members;
    }

    public void setMembers(List<ProjectMemberResponse> members) {
        this.members = members;
    }

    public List<ProjectReportResponse> getReports() {
        return reports;
    }

    public void setReports(List<ProjectReportResponse> reports) {
        this.reports = reports;
    }

    public ProjectSubmissionResponse getSubmission() {
        return submission;
    }

    public void setSubmission(ProjectSubmissionResponse submission) {
        this.submission = submission;
    }
}
