package vn.edu.epu.quanlyhoso.project.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.epu.quanlyhoso.project.entity.ProjectSubmission;

public interface ProjectSubmissionRepository extends JpaRepository<ProjectSubmission, Integer> {

    Optional<ProjectSubmission> findByProjectId(Integer projectId);

    boolean existsByProjectId(Integer projectId);
}
