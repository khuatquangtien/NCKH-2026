package vn.edu.epu.quanlyhoso.council.controller;

import java.util.List;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;  
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.edu.epu.quanlyhoso.council.dto.request.AssignTopicRequest;
import vn.edu.epu.quanlyhoso.council.dto.request.CreateCouncilRequest;
import vn.edu.epu.quanlyhoso.council.dto.request.CreateEvaluationRequest;
import vn.edu.epu.quanlyhoso.council.entity.Council;
import vn.edu.epu.quanlyhoso.council.entity.CouncilAssignment;
import vn.edu.epu.quanlyhoso.council.entity.CouncilEvaluation;
import vn.edu.epu.quanlyhoso.council.entity.CouncilMember;
import vn.edu.epu.quanlyhoso.council.service.CouncilService;

@RestController
@RequestMapping("/api/councils")
public class CouncilController {

    private final CouncilService councilService;

    public CouncilController(CouncilService councilService) {
        this.councilService = councilService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")  // ← THÊM: Chỉ ADMIN
    @PostMapping
    public ResponseEntity<Council> createCouncil(@Valid @RequestBody CreateCouncilRequest request) {
        Council createdCouncil = councilService.createCouncil(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCouncil);
    }

    @GetMapping  // ← KHÔNG cần phân quyền
    public ResponseEntity<List<Council>> findAllCouncils() {
        return ResponseEntity.ok(councilService.findAllCouncils());
    }

    @GetMapping("/{id}")  // ← KHÔNG cần phân quyền
    public ResponseEntity<Council> findCouncilById(@PathVariable Long id) {
        return ResponseEntity.ok(councilService.findCouncilById(id));
    }

    @GetMapping("/{id}/members")  // ← KHÔNG cần phân quyền
    public ResponseEntity<List<CouncilMember>> findMembersByCouncilId(@PathVariable Long id) {
        return ResponseEntity.ok(councilService.findMembersByCouncilId(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")  // ← THÊM: Chỉ ADMIN
    @PostMapping("/{councilId}/assignments")
    public ResponseEntity<CouncilAssignment> assignTopic(
            @PathVariable Long councilId,
            @Valid @RequestBody AssignTopicRequest request) {
        CouncilAssignment assignment = councilService.assignTopic(councilId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignment);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COUNCIL_MEMBER')")  // ← THÊM: ADMIN hoặc COUNCIL_MEMBER
    @PostMapping("/evaluations")
    public ResponseEntity<CouncilEvaluation> createEvaluation(@Valid @RequestBody CreateEvaluationRequest request) {
        CouncilEvaluation evaluation = councilService.createEvaluation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(evaluation);
    }
}