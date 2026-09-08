package vn.edu.epu.quanlyhoso.project.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.epu.quanlyhoso.project.entity.Project;
import vn.edu.epu.quanlyhoso.project.enums.ProjectStatus;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByFaculty(String faculty);

    List<Project> findByStatusAndFaculty(ProjectStatus status, String faculty);

    List<Project> findByTitleContainingIgnoreCase(String title);

    List<Project> findByLeaderId(Integer leaderId);
}
