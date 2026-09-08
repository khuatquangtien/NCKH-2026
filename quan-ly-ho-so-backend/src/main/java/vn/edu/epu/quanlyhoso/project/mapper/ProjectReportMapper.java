package vn.edu.epu.quanlyhoso.project.mapper;

import org.springframework.stereotype.Component;

import vn.edu.epu.quanlyhoso.project.dto.response.ProjectReportResponse;
import vn.edu.epu.quanlyhoso.project.entity.ProjectReport;

@Component
public class ProjectReportMapper {

    public ProjectReportResponse toResponse(ProjectReport report) {
        ProjectReportResponse response = new ProjectReportResponse();
        response.setId(report.getId());
        response.setProjectId(report.getProject().getId());
        response.setProgressPercentage(report.getProgressPercentage());
        response.setReportContent(report.getReportContent());
        response.setSubmittedAt(report.getSubmittedAt());
        return response;
    }
}
