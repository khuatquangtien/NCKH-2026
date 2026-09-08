package vn.edu.epu.quanlyhoso.project.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.epu.quanlyhoso.project.entity.ProjectMember;
import vn.edu.epu.quanlyhoso.project.entity.ProjectMemberId;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

    List<ProjectMember> findByProject_Id(Integer projectId);

    List<ProjectMember> findByLecturer_Id(Integer lecturerId);

    boolean existsByProject_IdAndLecturer_Id(Integer projectId, Integer lecturerId);
}
