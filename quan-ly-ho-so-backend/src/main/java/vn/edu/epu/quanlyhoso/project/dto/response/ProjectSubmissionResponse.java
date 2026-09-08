package vn.edu.epu.quanlyhoso.project.dto.response;

import java.time.LocalDateTime;

public class ProjectSubmissionResponse {

    private Integer id;

    private Integer projectId;

    private String summaryReport;

    private LocalDateTime submittedAt;

    public ProjectSubmissionResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getSummaryReport() {
        return summaryReport;
    }

    public void setSummaryReport(String summaryReport) {
        this.summaryReport = summaryReport;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
