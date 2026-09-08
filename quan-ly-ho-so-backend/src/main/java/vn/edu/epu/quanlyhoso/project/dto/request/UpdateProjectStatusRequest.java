package vn.edu.epu.quanlyhoso.project.dto.request;

import jakarta.validation.constraints.NotNull;
import vn.edu.epu.quanlyhoso.project.enums.ProjectStatus;

public class UpdateProjectStatusRequest {

    @NotNull
    private ProjectStatus status;

    private String reason;

    public UpdateProjectStatusRequest() {
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
