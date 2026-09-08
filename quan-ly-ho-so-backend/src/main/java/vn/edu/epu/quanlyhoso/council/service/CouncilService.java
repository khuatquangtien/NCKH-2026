package vn.edu.epu.quanlyhoso.council.service;

import java.util.List;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
public class CouncilService {

    private final CouncilRepository councilRepository;
    private final CouncilMemberRepository councilMemberRepository;
    private final CouncilAssignmentRepository councilAssignmentRepository;
    private final CouncilEvaluationRepository councilEvaluationRepository;

    public CouncilService(
            CouncilRepository councilRepository,
            CouncilMemberRepository councilMemberRepository,
            CouncilAssignmentRepository councilAssignmentRepository,
            CouncilEvaluationRepository councilEvaluationRepository) {
        this.councilRepository = councilRepository;
        this.councilMemberRepository = councilMemberRepository;
        this.councilAssignmentRepository = councilAssignmentRepository;
        this.councilEvaluationRepository = councilEvaluationRepository;
    }

    @Transactional
    public Council createCouncil(CreateCouncilRequest request) {
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

    public Council findCouncilById(Long id) {
        return councilRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Council not found with id: " + id));
    }

    public List<CouncilMember> findMembersByCouncilId(Long councilId) {
        findCouncilById(councilId);
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

        CouncilEvaluation evaluation = new CouncilEvaluation();
        evaluation.setCouncilId(request.getCouncilId());
        evaluation.setProjectId(request.getProjectId());
        evaluation.setScore(request.getScore());
        evaluation.setGrade(request.getGrade());
        evaluation.setMeetingMinutes(request.getMeetingMinutes());
        return councilEvaluationRepository.save(evaluation);
    }
}
