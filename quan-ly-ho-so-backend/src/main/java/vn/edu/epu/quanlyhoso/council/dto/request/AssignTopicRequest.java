package vn.edu.epu.quanlyhoso.council.dto.request;

import jakarta.validation.constraints.NotNull;

public class AssignTopicRequest {

    @NotNull(message = "Project id is required")
    private Long projectId;

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
}
