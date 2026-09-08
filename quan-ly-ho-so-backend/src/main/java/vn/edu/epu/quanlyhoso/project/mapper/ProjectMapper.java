package vn.edu.epu.quanlyhoso.project.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import vn.edu.epu.quanlyhoso.lecturer.entity.Lecturer;
import vn.edu.epu.quanlyhoso.project.dto.response.ProjectDetailResponse;
import vn.edu.epu.quanlyhoso.project.dto.response.ProjectMemberResponse;
import vn.edu.epu.quanlyhoso.project.dto.response.ProjectResponse;
import vn.edu.epu.quanlyhoso.project.entity.Project;
import vn.edu.epu.quanlyhoso.project.entity.ProjectMember;

@Component
public class ProjectMapper {

    private final ProjectReportMapper projectReportMapper;
    private final ProjectSubmissionMapper projectSubmissionMapper;

    public ProjectMapper(
            ProjectReportMapper projectReportMapper,
            ProjectSubmissionMapper projectSubmissionMapper) {
        this.projectReportMapper = projectReportMapper;
        this.projectSubmissionMapper = projectSubmissionMapper;
    }

    public ProjectResponse toResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setTitle(project.getTitle());
        response.setFaculty(project.getFaculty());
        response.setStatus(project.getStatus());
        response.setEstimatedBudget(project.getEstimatedBudget());
        response.setApprovedBudget(project.getApprovedBudget());
        response.setCreatedAt(project.getCreatedAt());

        Lecturer leader = project.getLeader();
        if (leader != null) {
            response.setLeaderId(leader.getId());
            response.setLeaderName(leader.getFullName());
        }

        return response;
    }

    public ProjectDetailResponse toDetailResponse(Project project) {
        ProjectDetailResponse response = new ProjectDetailResponse();
        response.setId(project.getId());
        response.setTitle(project.getTitle());
        response.setObjective(project.getObjective());
        response.setExpectedProduct(project.getExpectedProduct());
        response.setEstimatedBudget(project.getEstimatedBudget());
        response.setApprovedBudget(project.getApprovedBudget());
        response.setStatus(project.getStatus());
        response.setFaculty(project.getFaculty());
        response.setCreatedAt(project.getCreatedAt());

        Lecturer leader = project.getLeader();
        if (leader != null) {
            response.setLeaderId(leader.getId());
            response.setLeaderName(leader.getFullName());
        }

        response.setMembers(project.getMembers().stream()
                .map(this::toMemberResponse)
                .toList());
        response.setReports(project.getReports().stream()
                .map(projectReportMapper::toResponse)
                .toList());
        if (project.getSubmission() != null) {
            response.setSubmission(projectSubmissionMapper.toResponse(project.getSubmission()));
        }

        return response;
    }

    public ProjectMemberResponse toMemberResponse(ProjectMember projectMember) {
        return toMemberResponse(projectMember.getLecturer());
    }

    public ProjectMemberResponse toMemberResponse(Lecturer lecturer) {
        ProjectMemberResponse response = new ProjectMemberResponse();
        response.setLecturerId(lecturer.getId());
        response.setFullName(lecturer.getFullName());
        response.setFaculty(lecturer.getFaculty());
        response.setAcademicRank(lecturer.getAcademicRank());
        response.setAcademicDegree(lecturer.getAcademicDegree());
        return response;
    }

    public List<ProjectResponse> toResponseList(List<Project> projects) {
        return projects.stream()
                .map(this::toResponse)
                .toList();
    }

}
