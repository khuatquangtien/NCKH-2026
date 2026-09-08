package vn.edu.epu.quanlyhoso.council.dto.request;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import vn.edu.epu.quanlyhoso.council.entity.EvaluationLevel;

public class CreateEvaluationRequest {

    @NotNull(message = "Council id is required")
    private Long councilId;

    @NotNull(message = "Project id is required")
    private Long projectId;

    @NotNull(message = "Score is required")
    @DecimalMin(value = "0.0", message = "Score must be greater than or equal to 0")
    @DecimalMax(value = "10.0", message = "Score must be less than or equal to 10")
    private BigDecimal score;

    @NotNull(message = "Grade is required")
    private EvaluationLevel grade;

    private String meetingMinutes;

    public Long getCouncilId() { return councilId; }
    public void setCouncilId(Long councilId) { this.councilId = councilId; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public EvaluationLevel getGrade() { return grade; }
    public void setGrade(EvaluationLevel grade) { this.grade = grade; }
    public String getMeetingMinutes() { return meetingMinutes; }
    public void setMeetingMinutes(String meetingMinutes) { this.meetingMinutes = meetingMinutes; }
}
