package vn.edu.epu.quanlyhoso.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.epu.quanlyhoso.auth.entity.Account;
import vn.edu.epu.quanlyhoso.common.exception.ResourceNotFoundException;
import vn.edu.epu.quanlyhoso.common.security.CurrentUserService;
import vn.edu.epu.quanlyhoso.lecturer.entity.Lecturer;
import vn.edu.epu.quanlyhoso.lecturer.repository.LecturerRepository;
import vn.edu.epu.quanlyhoso.project.dto.request.SubmitProjectSubmissionRequest;
import vn.edu.epu.quanlyhoso.project.dto.response.ProjectSubmissionResponse;
import vn.edu.epu.quanlyhoso.project.entity.Project;
import vn.edu.epu.quanlyhoso.project.entity.ProjectSubmission;
import vn.edu.epu.quanlyhoso.project.enums.ProjectStatus;
import vn.edu.epu.quanlyhoso.project.exception.DuplicateSubmissionException;
import vn.edu.epu.quanlyhoso.project.exception.InvalidProjectStatusException;
import vn.edu.epu.quanlyhoso.project.mapper.ProjectSubmissionMapper;
import vn.edu.epu.quanlyhoso.project.repository.ProjectRepository;
import vn.edu.epu.quanlyhoso.project.repository.ProjectSubmissionRepository;
import vn.edu.epu.quanlyhoso.search.ProjectSearchIndexService;

@Service
public class ProjectSubmissionServiceImpl implements ProjectSubmissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectSubmissionServiceImpl.class);

    private final ProjectRepository projectRepository;
    private final ProjectSubmissionRepository projectSubmissionRepository;
    private final ProjectSubmissionMapper projectSubmissionMapper;
    private final ProjectSearchIndexService projectSearchIndexService;
    private final CurrentUserService currentUserService;
    private final LecturerRepository lecturerRepository;

    public ProjectSubmissionServiceImpl(
            ProjectRepository projectRepository,
            ProjectSubmissionRepository projectSubmissionRepository,
            ProjectSubmissionMapper projectSubmissionMapper,
            ProjectSearchIndexService projectSearchIndexService,
            CurrentUserService currentUserService,
            LecturerRepository lecturerRepository) {
        this.projectRepository = projectRepository;
        this.projectSubmissionRepository = projectSubmissionRepository;
        this.projectSubmissionMapper = projectSubmissionMapper;
        this.projectSearchIndexService = projectSearchIndexService;
        this.currentUserService = currentUserService;
        this.lecturerRepository = lecturerRepository;
    }

    @Override
    @Transactional
    public ProjectSubmissionResponse submitFinalSubmission(
            Integer projectId,
            SubmitProjectSubmissionRequest request) {
        Project project = findProjectById(projectId);
        if (project.getStatus() != ProjectStatus.ONGOING) {
            throw new InvalidProjectStatusException("Final submissions can only be submitted for ONGOING projects");
        }
        Lecturer currentLecturer = getCurrentLecturer();
        if (!isProjectLeader(project, currentLecturer)) {
            throw new AccessDeniedException("Only the project leader can submit the final submission");
        }
        // A project has only one final acceptance submission; the table also enforces this with a unique project_id.
        if (projectSubmissionRepository.existsByProjectId(projectId)) {
            throw new DuplicateSubmissionException("Final submission already exists for project id: " + projectId);
        }

        ProjectSubmission submission = new ProjectSubmission();
        submission.setProject(project);
        submission.setSummaryReport(request.getSummaryReport());
        ProjectSubmission savedSubmission = projectSubmissionRepository.save(submission);

        // Final acceptance is the only lecturer flow that completes an ONGOING project.
        project.setStatus(ProjectStatus.COMPLETED);
        project.setSubmission(savedSubmission);
        projectRepository.save(project);
        syncProjectIndex(project);

        return projectSubmissionMapper.toResponse(savedSubmission);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectSubmissionResponse getSubmissionByProject(Integer projectId) {
        // TODO: Apply lecturer/admin visibility rules after Security module is merged.
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + projectId);
        }
        ProjectSubmission submission = projectSubmissionRepository.findByProjectId(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Final submission not found for project id: " + projectId));
        return projectSubmissionMapper.toResponse(submission);
    }

    private Project findProjectById(Integer projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
    }

    private Lecturer getCurrentLecturer() {
        Account currentAccount = currentUserService.getCurrentAccount();
        return lecturerRepository.findByAccountId(currentAccount.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lecturer profile not found for current account"));
    }

    private boolean isProjectLeader(Project project, Lecturer lecturer) {
        return project.getLeader().getId().equals(lecturer.getId());
    }

    private void syncProjectIndex(Project project) {
        try {
            projectSearchIndexService.indexProject(project);
        } catch (RuntimeException exception) {
            LOGGER.warn("Failed to synchronize project search index for project id: {}", project.getId(), exception);
        }
    }
}
