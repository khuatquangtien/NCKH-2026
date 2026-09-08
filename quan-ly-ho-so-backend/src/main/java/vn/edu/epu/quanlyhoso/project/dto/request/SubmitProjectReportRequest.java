package vn.edu.epu.quanlyhoso.project.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class SubmitProjectReportRequest {

    @NotNull
    @Min(0)
    @Max(100)
    private Integer progressPercentage;

    private String reportContent;

    public SubmitProjectReportRequest() {
    }

    public Integer getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }
}
