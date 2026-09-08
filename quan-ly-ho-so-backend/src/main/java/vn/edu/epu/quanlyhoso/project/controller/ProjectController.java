package vn.edu.epu.quanlyhoso.project.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.edu.epu.quanlyhoso.project.dto.request.CreateProjectRequest;
import vn.edu.epu.quanlyhoso.project.dto.request.SubmitProjectReportRequest;
import vn.edu.epu.quanlyhoso.project.dto.request.SubmitProjectSubmissionRequest;
import vn.edu.epu.quanlyhoso.project.dto.response.ProjectDetailResponse;
import vn.edu.epu.quanlyhoso.project.dto.response.ProjectReportResponse;
import vn.edu.epu.quanlyhoso.project.dto.response.ProjectSubmissionResponse;
import vn.edu.epu.quanlyhoso.project.service.ProjectReportService;
import vn.edu.epu.quanlyhoso.project.service.ProjectService;
import vn.edu.epu.quanlyhoso.project.service.ProjectSubmissionService;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectReportService projectReportService;
    private final ProjectSubmissionService projectSubmissionService;

    public ProjectController(
            ProjectService projectService,
            ProjectReportService projectReportService,
            ProjectSubmissionService projectSubmissionService) {
        this.projectService = projectService;
        this.projectReportService = projectReportService;
        this.projectSubmissionService = projectSubmissionService;
    }

    @PostMapping
    public ResponseEntity<ProjectDetailResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponse> getProjectById(@PathVariable Integer projectId) {
        // TODO: Apply lecturer visibility rules after Security module is merged.
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    @PostMapping("/{projectId}/reports")
    public ResponseEntity<ProjectReportResponse> submitReport(
            @PathVariable Integer projectId,
            @Valid @RequestBody SubmitProjectReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectReportService.submitReport(projectId, request));
    }

    @GetMapping("/{projectId}/reports")
    public ResponseEntity<List<ProjectReportResponse>> getReportsByProject(@PathVariable Integer projectId) {
        // TODO: Apply lecturer/admin visibility rules after Security module is merged.
        return ResponseEntity.ok(projectReportService.getReportsByProject(projectId));
    }

    @PostMapping("/{projectId}/submissions")
    public ResponseEntity<ProjectSubmissionResponse> submitFinalSubmission(
            @PathVariable Integer projectId,
            @Valid @RequestBody SubmitProjectSubmissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectSubmissionService.submitFinalSubmission(projectId, request));
    }

    @GetMapping("/{projectId}/submissions")
    public ResponseEntity<ProjectSubmissionResponse> getSubmissionByProject(@PathVariable Integer projectId) {
        // TODO: Apply lecturer/admin visibility rules after Security module is merged.
        return ResponseEntity.ok(projectSubmissionService.getSubmissionByProject(projectId));
    }
}
