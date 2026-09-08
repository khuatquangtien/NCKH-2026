package vn.edu.epu.quanlyhoso.project.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.epu.quanlyhoso.auth.entity.Account;
import vn.edu.epu.quanlyhoso.auth.repository.AccountRepository;
import vn.edu.epu.quanlyhoso.common.exception.BusinessException;
import vn.edu.epu.quanlyhoso.common.exception.ResourceNotFoundException;
import vn.edu.epu.quanlyhoso.lecturer.entity.Lecturer;
import vn.edu.epu.quanlyhoso.lecturer.repository.LecturerRepository;
import vn.edu.epu.quanlyhoso.project.dto.request.CreateProjectRequest;
import vn.edu.epu.quanlyhoso.project.dto.request.UpdateProjectBudgetRequest;
import vn.edu.epu.quanlyhoso.project.dto.request.UpdateProjectStatusRequest;
import vn.edu.epu.quanlyhoso.project.dto.response.BudgetResponse;
import vn.edu.epu.quanlyhoso.project.dto.response.ProjectDetailResponse;
import vn.edu.epu.quanlyhoso.project.dto.response.ProjectResponse;
import vn.edu.epu.quanlyhoso.project.entity.Project;
import vn.edu.epu.quanlyhoso.project.entity.ProjectMember;
import vn.edu.epu.quanlyhoso.project.entity.ProjectMemberId;
import vn.edu.epu.quanlyhoso.project.enums.ProjectStatus;
import vn.edu.epu.quanlyhoso.project.exception.InvalidBudgetException;
import vn.edu.epu.quanlyhoso.project.exception.InvalidProjectStatusException;
import vn.edu.epu.quanlyhoso.project.mapper.BudgetMapper;
import vn.edu.epu.quanlyhoso.project.mapper.ProjectMapper;
import vn.edu.epu.quanlyhoso.project.repository.ProjectMemberRepository;
import vn.edu.epu.quanlyhoso.project.repository.ProjectRepository;
import vn.edu.epu.quanlyhoso.search.ProjectSearchIndexService;
import vn.edu.epu.quanlyhoso.search.dto.ProjectSearchCriteria;

@Service
public class ProjectServiceImpl implements ProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;
    private final AccountRepository accountRepository;
    private final LecturerRepository lecturerRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMapper projectMapper;
    private final BudgetMapper budgetMapper;
    private final ProjectSearchIndexService projectSearchIndexService;

    public ProjectServiceImpl(
            ProjectRepository projectRepository,
            AccountRepository accountRepository,
            LecturerRepository lecturerRepository,
            ProjectMemberRepository projectMemberRepository,
            ProjectMapper projectMapper,
            BudgetMapper budgetMapper,
            ProjectSearchIndexService projectSearchIndexService) {
        this.projectRepository = projectRepository;
        this.accountRepository = accountRepository;
        this.lecturerRepository = lecturerRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectMapper = projectMapper;
        this.budgetMapper = budgetMapper;
        this.projectSearchIndexService = projectSearchIndexService;
    }

    @Override
    @Transactional
    public ProjectDetailResponse createProject(CreateProjectRequest request) {
        Lecturer leader = findCurrentLecturer();
        validateEstimatedBudget(request.getEstimatedBudget());

        Project project = new Project();
        project.setTitle(request.getTitle());
        project.setObjective(request.getObjective());
        project.setExpectedProduct(request.getExpectedProduct());
        project.setEstimatedBudget(request.getEstimatedBudget());
        project.setFaculty(request.getFaculty());
        project.setLeader(leader);
        project.setStatus(ProjectStatus.PENDING);

        Project savedProject = projectRepository.save(project);
        List<ProjectMember> members = createProjectMembers(savedProject, request.getMemberIds(), leader.getId());
        if (!members.isEmpty()) {
            projectMemberRepository.saveAll(members);
            savedProject.setMembers(members);
        }

        syncProjectIndex(savedProject);
        return projectMapper.toDetailResponse(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> searchProjects(
            ProjectStatus status,
            String faculty,
            String keyword,
            BigDecimal minEstimatedBudget,
            BigDecimal maxEstimatedBudget,
            BigDecimal minApprovedBudget,
            BigDecimal maxApprovedBudget) {
        // TODO: Restrict this operation to ROLE_ADMIN after Security module is merged.
        validateBudgetRange(minEstimatedBudget, maxEstimatedBudget, "estimated budget");
        validateBudgetRange(minApprovedBudget, maxApprovedBudget, "approved budget");

        ProjectSearchCriteria criteria = buildSearchCriteria(
                status,
                faculty,
                keyword,
                minEstimatedBudget,
                maxEstimatedBudget,
                minApprovedBudget,
                maxApprovedBudget);

        if (hasLuceneCriteria(criteria)) {
            try {
                // Lucene is only the search layer: it returns candidate IDs, then MySQL reloads source-of-truth data.
                List<Integer> projectIds = projectSearchIndexService.searchProjectIds(criteria);
                List<Project> luceneProjects = loadProjectsByIdsPreservingOrder(projectIds);
                // Keyword and budget conditions already ran in Lucene; rechecking keyword here can drop fuzzy/accent-insensitive hits.
                return projectMapper.toResponseList(filterNonLuceneFilters(luceneProjects, criteria));
            } catch (RuntimeException exception) {
                LOGGER.warn("Lucene project search failed. Falling back to MySQL filtering.", exception);
            }
        }

        return projectMapper.toResponseList(fallbackSearchProjects(criteria));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDetailResponse getProjectById(Integer projectId) {
        // TODO: Apply lecturer/admin visibility rules after Security module is merged.
        Project project = findProjectById(projectId);
        return projectMapper.toDetailResponse(project);
    }

    @Override
    @Transactional
    public ProjectDetailResponse updateProjectStatus(Integer projectId, UpdateProjectStatusRequest request) {
        // TODO: Restrict this operation to ROLE_ADMIN after Security module is merged.
        Project project = findProjectById(projectId);
        ProjectStatus targetStatus = request.getStatus();
        validateAdminStatusTransition(project.getStatus(), targetStatus);
        project.setStatus(targetStatus);
        Project updatedProject = projectRepository.save(project);
        syncProjectIndex(updatedProject);
        return projectMapper.toDetailResponse(updatedProject);
    }

    @Override
    @Transactional
    public BudgetResponse updateApprovedBudget(Integer projectId, UpdateProjectBudgetRequest request) {
        // TODO: Restrict this operation to ROLE_ADMIN after Security module is merged.
        Project project = findProjectById(projectId);
        BigDecimal approvedBudget = request.getApprovedBudget();
        if (approvedBudget == null || approvedBudget.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidBudgetException("Approved budget must not be negative");
        }
        project.setApprovedBudget(approvedBudget);
        Project updatedProject = projectRepository.save(project);
        syncProjectIndex(updatedProject);
        return budgetMapper.toResponse(updatedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetResponse getProjectBudget(Integer projectId) {
        // TODO: Restrict this operation to ROLE_ADMIN after Security module is merged.
        return budgetMapper.toResponse(findProjectById(projectId));
    }

    private List<Project> findProjectsByStatusAndFaculty(ProjectStatus status, String faculty) {
        boolean hasStatus = status != null;
        boolean hasFaculty = faculty != null && !faculty.isBlank();
        if (hasStatus && hasFaculty) {
            return projectRepository.findByStatusAndFaculty(status, faculty);
        }
        if (hasStatus) {
            return projectRepository.findByStatus(status);
        }
        if (hasFaculty) {
            return projectRepository.findByFaculty(faculty);
        }
        return projectRepository.findAll();
    }

    private ProjectSearchCriteria buildSearchCriteria(
            ProjectStatus status,
            String faculty,
            String keyword,
            BigDecimal minEstimatedBudget,
            BigDecimal maxEstimatedBudget,
            BigDecimal minApprovedBudget,
            BigDecimal maxApprovedBudget) {
        ProjectSearchCriteria criteria = new ProjectSearchCriteria();
        criteria.setStatus(status);
        criteria.setFaculty(faculty);
        criteria.setKeyword(keyword);
        criteria.setMinEstimatedBudget(minEstimatedBudget);
        criteria.setMaxEstimatedBudget(maxEstimatedBudget);
        criteria.setMinApprovedBudget(minApprovedBudget);
        criteria.setMaxApprovedBudget(maxApprovedBudget);
        return criteria;
    }

    private boolean hasLuceneCriteria(ProjectSearchCriteria criteria) {
        return normalizeKeyword(criteria.getKeyword()) != null
                || criteria.getMinEstimatedBudget() != null
                || criteria.getMaxEstimatedBudget() != null
                || criteria.getMinApprovedBudget() != null
                || criteria.getMaxApprovedBudget() != null;
    }

    private List<Project> loadProjectsByIdsPreservingOrder(List<Integer> projectIds) {
        if (projectIds.isEmpty()) {
            return List.of();
        }
        Map<Integer, Project> projectsById = projectRepository.findAllById(projectIds).stream()
                .collect(Collectors.toMap(Project::getId, Function.identity()));
        return projectIds.stream()
                .map(projectsById::get)
                .filter(project -> project != null)
                .toList();
    }

    // Fallback mode stays database/service based for resilience when the Lucene index is missing or unavailable.
    private List<Project> fallbackSearchProjects(ProjectSearchCriteria criteria) {
        return filterProjects(
                findProjectsByStatusAndFaculty(criteria.getStatus(), criteria.getFaculty()),
                criteria);
    }

    // After Lucene search, only non-Lucene filters are applied here.
    private List<Project> filterNonLuceneFilters(List<Project> projects, ProjectSearchCriteria criteria) {
        String normalizedFaculty = normalizeKeyword(criteria.getFaculty());
        return projects.stream()
                .filter(project -> criteria.getStatus() == null || project.getStatus() == criteria.getStatus())
                .filter(project -> normalizedFaculty == null
                        || normalizedFaculty.equals(normalizeKeyword(project.getFaculty())))
                .toList();
    }

    private List<Project> filterProjects(List<Project> projects, ProjectSearchCriteria criteria) {
        String normalizedKeyword = normalizeKeyword(criteria.getKeyword());
        String normalizedFaculty = normalizeKeyword(criteria.getFaculty());
        return projects.stream()
                .filter(project -> criteria.getStatus() == null || project.getStatus() == criteria.getStatus())
                .filter(project -> normalizedFaculty == null
                        || normalizedFaculty.equals(normalizeKeyword(project.getFaculty())))
                .filter(project -> normalizedKeyword == null || containsKeyword(project, normalizedKeyword))
                .filter(project -> isWithinRange(
                        project.getEstimatedBudget(),
                        criteria.getMinEstimatedBudget(),
                        criteria.getMaxEstimatedBudget()))
                .filter(project -> isWithinRange(
                        project.getApprovedBudget(),
                        criteria.getMinApprovedBudget(),
                        criteria.getMaxApprovedBudget()))
                .toList();
    }

    private boolean containsKeyword(Project project, String normalizedKeyword) {
        return containsKeyword(project.getTitle(), normalizedKeyword)
                || containsKeyword(project.getObjective(), normalizedKeyword)
                || containsKeyword(project.getExpectedProduct(), normalizedKeyword);
    }

    private boolean containsKeyword(String value, String normalizedKeyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedKeyword);
    }

    private boolean isWithinRange(BigDecimal value, BigDecimal minValue, BigDecimal maxValue) {
        if (minValue == null && maxValue == null) {
            return true;
        }
        if (value == null) {
            return false;
        }
        if (minValue != null && value.compareTo(minValue) < 0) {
            return false;
        }
        return maxValue == null || value.compareTo(maxValue) <= 0;
    }

    private void validateBudgetRange(BigDecimal minValue, BigDecimal maxValue, String fieldName) {
        if (minValue != null && minValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidBudgetException("Minimum " + fieldName + " must not be negative");
        }
        if (maxValue != null && maxValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidBudgetException("Maximum " + fieldName + " must not be negative");
        }
        if (minValue != null && maxValue != null && minValue.compareTo(maxValue) > 0) {
            throw new InvalidBudgetException("Minimum " + fieldName + " must not be greater than maximum");
        }
    }

    private void syncProjectIndex(Project project) {
        try {
            projectSearchIndexService.indexProject(project);
        } catch (RuntimeException exception) {
            LOGGER.warn("Failed to synchronize project search index for project id: {}", project.getId(), exception);
        }
    }

    private List<ProjectMember> createProjectMembers(Project project, List<Integer> memberIds, Integer leaderId) {
        if (memberIds == null || memberIds.isEmpty()) {
            return List.of();
        }

        // memberIds are cooperating lecturers only, so the leader is stored separately and must not appear here.
        Set<Integer> uniqueMemberIds = new HashSet<>();
        List<ProjectMember> members = new ArrayList<>();
        for (Integer memberId : memberIds) {
            if (memberId == null) {
                throw new BusinessException("Project member id must not be null");
            }
            if (!uniqueMemberIds.add(memberId)) {
                throw new BusinessException("Duplicate project member id: " + memberId);
            }
            // Duplicate/self-membership checks happen before loading lecturers to fail fast on invalid request shape.
            if (memberId.equals(leaderId)) {
                throw new BusinessException("Project leader must not be included in cooperating lecturer ids");
            }
            Lecturer lecturer = findLecturerById(memberId);
            ProjectMember member = new ProjectMember();
            member.setId(new ProjectMemberId(project.getId(), lecturer.getId()));
            member.setProject(project);
            member.setLecturer(lecturer);
            members.add(member);
        }
        return members;
    }

    private void validateAdminStatusTransition(ProjectStatus currentStatus, ProjectStatus targetStatus) {
        // Admin approval/rejection only supports PENDING -> ONGOING and PENDING -> REJECTED.
        if (targetStatus != ProjectStatus.ONGOING && targetStatus != ProjectStatus.REJECTED) {
            throw new InvalidProjectStatusException("Admin status update only supports ONGOING or REJECTED");
        }
        if (currentStatus != ProjectStatus.PENDING) {
            throw new InvalidProjectStatusException("Only PENDING projects can be approved or rejected");
        }
    }

    private void validateEstimatedBudget(BigDecimal estimatedBudget) {
        if (estimatedBudget == null || estimatedBudget.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBudgetException("Estimated budget must be greater than zero");
        }
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.toLowerCase(Locale.ROOT);
    }

    private Lecturer findLecturerById(Integer lecturerId) {
        return lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with id: " + lecturerId));
    }

    private Lecturer findCurrentLecturer() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account currentAccount = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current account not found"));
        return lecturerRepository.findByAccountId(currentAccount.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lecturer profile not found for current account"));
    }

    private Project findProjectById(Integer projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
    }
}
