package vn.edu.epu.quanlyhoso.council.controller;

import java.util.List;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;  
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import vn.edu.epu.quanlyhoso.council.dto.response.MyCouncilResponse;
import vn.edu.epu.quanlyhoso.council.dto.request.UpdateCouncilRequest;
import vn.edu.epu.quanlyhoso.council.dto.request.UpdateCouncilStatusRequest;
import vn.edu.epu.quanlyhoso.council.dto.request.CouncilMemberRequest;

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

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Council>> findAllCouncils() {
        return ResponseEntity.ok(councilService.findAllCouncils());
    }

    @PreAuthorize("hasAuthority('LECTURER')")
    @GetMapping("/me")
    public ResponseEntity<List<MyCouncilResponse>> findMine() { return ResponseEntity.ok(councilService.findMyCouncils()); }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Council> update(@PathVariable Long id, @Valid @RequestBody UpdateCouncilRequest request) {
        return ResponseEntity.ok(councilService.updateCouncil(id, request));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Council> status(@PathVariable Long id, @Valid @RequestBody UpdateCouncilStatusRequest request) {
        return ResponseEntity.ok(councilService.updateStatus(id, request));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { councilService.deleteCouncil(id); return ResponseEntity.noContent().build(); }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/members")
    public ResponseEntity<CouncilMember> addMember(@PathVariable Long id, @Valid @RequestBody CouncilMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(councilService.addMember(id, request));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}/members/{lecturerId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long id, @PathVariable Long lecturerId) {
        councilService.removeMember(id, lecturerId); return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/assignments")
    public ResponseEntity<List<CouncilAssignment>> assignments(@PathVariable Long id) { return ResponseEntity.ok(councilService.findAssignments(id)); }

    @GetMapping("/{id}/evaluations")
    public ResponseEntity<List<CouncilEvaluation>> evaluations(@PathVariable Long id) { return ResponseEntity.ok(councilService.findEvaluations(id)); }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}/assignments/{projectId}")
    public ResponseEntity<Void> removeAssignment(@PathVariable Long id, @PathVariable Long projectId) {
        councilService.removeAssignment(id, projectId); return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")  // ← KHÔNG cần phân quyền
    public ResponseEntity<Council> findCouncilById(@PathVariable Long id) {
        return ResponseEntity.ok(councilService.findVisibleCouncilById(id));
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

    @PreAuthorize("hasAuthority('LECTURER')")
    @PostMapping("/evaluations")
    public ResponseEntity<CouncilEvaluation> createEvaluation(@Valid @RequestBody CreateEvaluationRequest request) {
        CouncilEvaluation evaluation = councilService.createEvaluation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(evaluation);
    }
}
