package vn.edu.epu.quanlyhoso.project.service;

import java.math.BigDecimal;
import java.util.List;

import vn.edu.epu.quanlyhoso.project.dto.request.CreateProjectRequest;
import vn.edu.epu.quanlyhoso.project.dto.request.UpdateProjectBudgetRequest;
import vn.edu.epu.quanlyhoso.project.dto.request.UpdateProjectStatusRequest;
import vn.edu.epu.quanlyhoso.project.dto.response.BudgetResponse;
import vn.edu.epu.quanlyhoso.project.dto.response.ProjectDetailResponse;
import vn.edu.epu.quanlyhoso.project.dto.response.ProjectResponse;
import vn.edu.epu.quanlyhoso.project.enums.ProjectStatus;

public interface ProjectService {

    ProjectDetailResponse createProject(CreateProjectRequest request);

    List<ProjectResponse> searchProjects(
            ProjectStatus status,
            String faculty,
            String keyword,
            BigDecimal minEstimatedBudget,
            BigDecimal maxEstimatedBudget,
            BigDecimal minApprovedBudget,
            BigDecimal maxApprovedBudget);

    ProjectDetailResponse getProjectById(Integer projectId);

    ProjectDetailResponse updateProjectStatus(Integer projectId, UpdateProjectStatusRequest request);

    BudgetResponse updateApprovedBudget(Integer projectId, UpdateProjectBudgetRequest request);

    BudgetResponse getProjectBudget(Integer projectId);
}
