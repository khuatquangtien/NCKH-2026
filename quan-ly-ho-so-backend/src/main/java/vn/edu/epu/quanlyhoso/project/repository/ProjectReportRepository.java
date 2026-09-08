package vn.edu.epu.quanlyhoso.project.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.epu.quanlyhoso.project.entity.ProjectReport;

public interface ProjectReportRepository extends JpaRepository<ProjectReport, Integer> {

    List<ProjectReport> findByProjectId(Integer projectId);

    List<ProjectReport> findByProjectIdOrderBySubmittedAtDesc(Integer projectId);
}
