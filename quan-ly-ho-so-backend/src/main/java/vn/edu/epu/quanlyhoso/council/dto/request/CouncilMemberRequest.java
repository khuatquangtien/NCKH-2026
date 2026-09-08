package vn.edu.epu.quanlyhoso.council.dto.request;

import jakarta.validation.constraints.NotNull;
import vn.edu.epu.quanlyhoso.council.entity.CouncilRole;

public class CouncilMemberRequest {

    @NotNull(message = "Lecturer id is required")
    private Long lecturerId;

    @NotNull(message = "Council position is required")
    private CouncilRole position;

    public Long getLecturerId() { return lecturerId; }
    public void setLecturerId(Long lecturerId) { this.lecturerId = lecturerId; }
    public CouncilRole getPosition() { return position; }
    public void setPosition(CouncilRole position) { this.position = position; }
}
