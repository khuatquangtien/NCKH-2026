package vn.edu.epu.quanlyhoso.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProjectMemberId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "lecturer_id")
    private Integer lecturerId;

    public ProjectMemberId() {
    }

    public ProjectMemberId(Integer projectId, Integer lecturerId) {
        this.projectId = projectId;
        this.lecturerId = lecturerId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(Integer lecturerId) {
        this.lecturerId = lecturerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectMemberId that)) {
            return false;
        }
        return Objects.equals(projectId, that.projectId)
                && Objects.equals(lecturerId, that.lecturerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, lecturerId);
    }
}
