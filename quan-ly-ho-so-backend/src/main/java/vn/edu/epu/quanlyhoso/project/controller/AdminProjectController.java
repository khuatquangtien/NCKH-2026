package vn.edu.epu.quanlyhoso.project.controller;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.edu.epu.quanlyhoso.project.dto.request.UpdateProjectBudgetRequest;
import vn.edu.epu.quanlyhoso.project.dto.request.UpdateProjectStatusRequest;
import vn.edu.epu.quanlyhoso.project.dto.response.BudgetResponse;
import vn.edu.epu.quanlyhoso.project.dto.response.ProjectDetailResponse;
import vn.edu.epu.quanlyhoso.project.dto.response.ProjectResponse;
import vn.edu.epu.quanlyhoso.project.enums.ProjectStatus;
import vn.edu.epu.quanlyhoso.project.service.ProjectService;
import vn.edu.epu.quanlyhoso.search.ProjectSearchIndexService;
import vn.edu.epu.quanlyhoso.search.dto.ProjectSearchIndexRebuildResponse;

@RestController
@RequestMapping("/api/admin/projects")
public class AdminProjectController {

    private final ProjectService projectService;
    private final ProjectSearchIndexService projectSearchIndexService;

    public AdminProjectController(
            ProjectService projectService,
            ProjectSearchIndexService projectSearchIndexService) {
        this.projectService = projectService;
        this.projectSearchIndexService = projectSearchIndexService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> searchProjects(
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) String faculty,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minEstimatedBudget,
            @RequestParam(required = false) BigDecimal maxEstimatedBudget,
            @RequestParam(required = false) BigDecimal minApprovedBudget,
            @RequestParam(required = false) BigDecimal maxApprovedBudget) {
        // TODO: Restrict this operation to ROLE_ADMIN after Security module is merged.
        return ResponseEntity.ok(projectService.searchProjects(
                status,
                faculty,
                keyword,
                minEstimatedBudget,
                maxEstimatedBudget,
                minApprovedBudget,
                maxApprovedBudget));
    }

    @PostMapping("/search-index/rebuild")
    public ResponseEntity<ProjectSearchIndexRebuildResponse> rebuildProjectSearchIndex() {
        // TODO: Restrict this operation to ROLE_ADMIN after Security module is merged.
        return ResponseEntity.ok(projectSearchIndexService.rebuildIndex());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponse> getProjectById(@PathVariable Integer projectId) {
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    @PatchMapping("/{projectId}/status")
    public ResponseEntity<ProjectDetailResponse> updateProjectStatus(
            @PathVariable Integer projectId,
            @Valid @RequestBody UpdateProjectStatusRequest request) {
        return ResponseEntity.ok(projectService.updateProjectStatus(projectId, request));
    }

    @GetMapping("/{projectId}/budget")
    public ResponseEntity<BudgetResponse> getProjectBudget(@PathVariable Integer projectId) {
        return ResponseEntity.ok(projectService.getProjectBudget(projectId));
    }

    @PatchMapping("/{projectId}/budget")
    public ResponseEntity<BudgetResponse> updateApprovedBudget(
            @PathVariable Integer projectId,
            @Valid @RequestBody UpdateProjectBudgetRequest request) {
        return ResponseEntity.ok(projectService.updateApprovedBudget(projectId, request));
    }
}

