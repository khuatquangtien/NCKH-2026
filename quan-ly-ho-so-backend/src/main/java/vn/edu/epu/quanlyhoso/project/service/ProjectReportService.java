package vn.edu.epu.quanlyhoso.project.service;

import java.util.List;

import vn.edu.epu.quanlyhoso.project.dto.request.SubmitProjectReportRequest;
import vn.edu.epu.quanlyhoso.project.dto.response.ProjectReportResponse;

public interface ProjectReportService {

    ProjectReportResponse submitReport(Integer projectId, SubmitProjectReportRequest request);

    List<ProjectReportResponse> getReportsByProject(Integer projectId);
}
