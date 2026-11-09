package vn.edu.epu.quanlyhoso.council.service;

import java.util.List;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import vn.edu.epu.quanlyhoso.council.dto.request.AssignTopicRequest;
import vn.edu.epu.quanlyhoso.council.dto.request.CouncilMemberRequest;
import vn.edu.epu.quanlyhoso.council.dto.request.CreateCouncilRequest;
import vn.edu.epu.quanlyhoso.council.dto.request.CreateEvaluationRequest;
import vn.edu.epu.quanlyhoso.council.entity.Council;
import vn.edu.epu.quanlyhoso.council.entity.CouncilAssignment;
import vn.edu.epu.quanlyhoso.council.entity.CouncilEvaluation;
import vn.edu.epu.quanlyhoso.council.entity.CouncilMember;
import vn.edu.epu.quanlyhoso.council.repository.CouncilAssignmentRepository;
import vn.edu.epu.quanlyhoso.council.repository.CouncilEvaluationRepository;
import vn.edu.epu.quanlyhoso.council.repository.CouncilMemberRepository;
import vn.edu.epu.quanlyhoso.council.repository.CouncilRepository;
import vn.edu.epu.quanlyhoso.auth.entity.Account;
import vn.edu.epu.quanlyhoso.common.exception.ProfileIncompleteException;
import vn.edu.epu.quanlyhoso.common.security.CurrentUserService;
import vn.edu.epu.quanlyhoso.lecturer.entity.Lecturer;
import vn.edu.epu.quanlyhoso.lecturer.repository.LecturerRepository;
import vn.edu.epu.quanlyhoso.lecturer.service.CurrentLecturerService;
import vn.edu.epu.quanlyhoso.council.dto.response.MyCouncilResponse;
import vn.edu.epu.quanlyhoso.council.dto.request.UpdateCouncilRequest;
import vn.edu.epu.quanlyhoso.council.dto.request.UpdateCouncilStatusRequest;
import vn.edu.epu.quanlyhoso.council.entity.CouncilMemberId;

@Service
public class CouncilService {

    private final CouncilRepository councilRepository;
    private final CouncilMemberRepository councilMemberRepository;
    private final CouncilAssignmentRepository councilAssignmentRepository;
    private final CouncilEvaluationRepository councilEvaluationRepository;
    private final CurrentUserService currentUserService;
    private final LecturerRepository lecturerRepository;
    private final CurrentLecturerService currentLecturerService;

    public CouncilService(
            CouncilRepository councilRepository,
            CouncilMemberRepository councilMemberRepository,
            CouncilAssignmentRepository councilAssignmentRepository,
            CouncilEvaluationRepository councilEvaluationRepository,
            CurrentUserService currentUserService,
            LecturerRepository lecturerRepository,
            CurrentLecturerService currentLecturerService) {
        this.councilRepository = councilRepository;
        this.councilMemberRepository = councilMemberRepository;
        this.councilAssignmentRepository = councilAssignmentRepository;
        this.councilEvaluationRepository = councilEvaluationRepository;
        this.currentUserService = currentUserService;
        this.lecturerRepository = lecturerRepository;
        this.currentLecturerService = currentLecturerService;
    }

    @Transactional
    public Council createCouncil(CreateCouncilRequest request) {
        long chairmen = request.getMembers().stream()
                .filter(m -> m.getPosition() == vn.edu.epu.quanlyhoso.council.entity.CouncilRole.CHAIRMAN).count();
        long secretaries = request.getMembers().stream()
                .filter(m -> m.getPosition() == vn.edu.epu.quanlyhoso.council.entity.CouncilRole.SECRETARY).count();
        long unique = request.getMembers().stream().map(CouncilMemberRequest::getLecturerId).distinct().count();
        if (chairmen != 1 || secretaries != 1)
            throw new IllegalArgumentException("Council requires exactly one chairman and one secretary");
        if (unique != request.getMembers().size())
            throw new IllegalArgumentException("A lecturer cannot appear twice in one council");
        request.getMembers().forEach(m -> lecturerRepository.findById(m.getLecturerId().intValue())
                .orElseThrow(() -> new EntityNotFoundException("Lecturer not found: " + m.getLecturerId())));
        Council council = new Council();
        council.setName(request.getName());
        Council savedCouncil = councilRepository.save(council);

        for (CouncilMemberRequest memberRequest : request.getMembers()) {
            CouncilMember councilMember = new CouncilMember();
            councilMember.setCouncilId(savedCouncil.getId());
            councilMember.setLecturerId(memberRequest.getLecturerId());
            councilMember.setPosition(memberRequest.getPosition());
            councilMemberRepository.save(councilMember);
        }

        return savedCouncil;
    }

    public List<Council> findAllCouncils() {
        return councilRepository.findAll();
    }

    public List<MyCouncilResponse> findMyCouncils() {
        Lecturer lecturer = currentLecturerService.getCurrentLecturerOrThrow();
        return councilMemberRepository.findByLecturerId(lecturer.getId().longValue()).stream()
                .map(m -> {
                    Council c = findCouncilById(m.getCouncilId());
                    return new MyCouncilResponse(c.getId(), c.getName(), c.getStatus(), m.getPosition(),
                            c.getCreatedAt());
                })
                .toList();
    }

    @Transactional
    public Council updateCouncil(Long id, UpdateCouncilRequest request) {
        Council council = findCouncilById(id);
        council.setName(request.name());
        return councilRepository.save(council);
    }

    @Transactional
    public Council updateStatus(Long id, UpdateCouncilStatusRequest request) {
        Council council = findCouncilById(id);
        council.setStatus(request.status());
        return councilRepository.save(council);
    }

    @Transactional
    public CouncilMember addMember(Long id, CouncilMemberRequest request) {
        findCouncilById(id);
        lecturerRepository.findById(request.getLecturerId().intValue())
                .orElseThrow(() -> new EntityNotFoundException("Lecturer not found"));
        CouncilMember member = new CouncilMember();
        member.setCouncilId(id);
        member.setLecturerId(request.getLecturerId());
        member.setPosition(request.getPosition());
        return councilMemberRepository.save(member);
    }

    @Transactional
    public void removeMember(Long id, Long lecturerId) {
        councilMemberRepository.deleteById(new CouncilMemberId(id, lecturerId));
    }

    @Transactional
    public void deleteCouncil(Long id) {
        councilRepository.delete(findCouncilById(id));
    }

    public List<CouncilAssignment> findAssignments(Long id) {
        assertCouncilVisible(id);
        return councilAssignmentRepository.findByCouncilId(id);
    }

    public List<CouncilEvaluation> findEvaluations(Long id) {
        assertCouncilVisible(id);
        return councilEvaluationRepository.findByCouncilId(id);
    }

    @Transactional
    public void removeAssignment(Long id, Long projectId) {
        findCouncilById(id);
        councilAssignmentRepository.deleteByCouncilIdAndProjectId(id, projectId);
    }

    public Council findCouncilById(Long id) {
        return councilRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Council not found with id: " + id));
    }

    public Council findVisibleCouncilById(Long id) {
        assertCouncilVisible(id);
        return findCouncilById(id);
    }

    public List<CouncilMember> findMembersByCouncilId(Long councilId) {
        assertCouncilVisible(councilId);
        return councilMemberRepository.findByCouncilId(councilId);
    }

    @Transactional
    public CouncilAssignment assignTopic(Long councilId, AssignTopicRequest request) {
        findCouncilById(councilId);

        if (councilAssignmentRepository.findByProjectId(request.getProjectId()).isPresent()) {
            throw new IllegalArgumentException("Project has already been assigned to a council");
        }

        CouncilAssignment assignment = new CouncilAssignment();
        assignment.setCouncilId(councilId);
        assignment.setProjectId(request.getProjectId());
        return councilAssignmentRepository.save(assignment);
    }

    @Transactional
    public CouncilEvaluation createEvaluation(CreateEvaluationRequest request) {
        findCouncilById(request.getCouncilId());

        CouncilAssignment assignment = councilAssignmentRepository.findByProjectId(request.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project has not been assigned to a council"));

        if (!assignment.getCouncilId().equals(request.getCouncilId())) {
            throw new IllegalArgumentException("Project is not assigned to this council");
        }

        Lecturer lecturer = currentLecturerService.getCurrentLecturerOrThrow();
        if (!isAdmin() && !councilMemberRepository.existsByCouncilIdAndLecturerId(request.getCouncilId(),
                lecturer.getId().longValue())) {
            throw new AccessDeniedException("Only assigned council members can evaluate this project");
        }
        CouncilEvaluation evaluation = councilEvaluationRepository
                .findByCouncilIdAndProjectIdAndEvaluatorId(request.getCouncilId(), request.getProjectId(),
                        lecturer.getId().longValue())
                .orElseGet(CouncilEvaluation::new);
        evaluation.setCouncilId(request.getCouncilId());
        evaluation.setProjectId(request.getProjectId());
        evaluation.setEvaluatorId(lecturer.getId().longValue());
        evaluation.setScore(request.getScore());
        evaluation.setGrade(request.getGrade());
        evaluation.setMeetingMinutes(request.getMeetingMinutes());
        return councilEvaluationRepository.save(evaluation);
    }

    private boolean isAdmin() {
        return "ADMIN".equals(currentUserService.getCurrentAccount().getRole().getRoleName());
    }

    private void assertCouncilVisible(Long id) {
        findCouncilById(id);
        if (!isAdmin()
                && !councilMemberRepository.existsByCouncilIdAndLecturerId(id,
                        currentLecturerService.getCurrentLecturerOrThrow().getId().longValue()))
            throw new AccessDeniedException("You are not a member of this council");
    }
}
