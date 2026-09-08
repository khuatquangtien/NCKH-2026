package vn.edu.epu.quanlyhoso.project.service;

import vn.edu.epu.quanlyhoso.project.dto.request.SubmitProjectSubmissionRequest;
import vn.edu.epu.quanlyhoso.project.dto.response.ProjectSubmissionResponse;

public interface ProjectSubmissionService {

    ProjectSubmissionResponse submitFinalSubmission(Integer projectId, SubmitProjectSubmissionRequest request);

    ProjectSubmissionResponse getSubmissionByProject(Integer projectId);
}
