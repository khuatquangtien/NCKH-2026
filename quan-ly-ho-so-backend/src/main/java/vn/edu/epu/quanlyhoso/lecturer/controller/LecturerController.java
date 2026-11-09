package vn.edu.epu.quanlyhoso.lecturer.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import vn.edu.epu.quanlyhoso.common.exception.ProfileIncompleteException;
import vn.edu.epu.quanlyhoso.common.exception.ResourceNotFoundException;
import vn.edu.epu.quanlyhoso.lecturer.dto.LecturerSummaryResponse;
import vn.edu.epu.quanlyhoso.lecturer.entity.Lecturer;
import vn.edu.epu.quanlyhoso.lecturer.repository.LecturerRepository;

@RestController
@RequestMapping("/api/lecturers")
@PreAuthorize("hasAnyAuthority('ADMIN','LECTURER')")
public class LecturerController {
    private final LecturerRepository repository;

    public LecturerController(LecturerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<LecturerSummaryResponse> find(@RequestParam(required = false) String keyword) {
        List<Lecturer> rows = keyword == null || keyword.isBlank() ? repository.findAll()
                : repository.findByFullNameContainingIgnoreCaseOrFacultyContainingIgnoreCase(keyword, keyword);
        return rows.stream().map(this::map).toList();
    }

    @GetMapping("/{id}")
    public LecturerSummaryResponse findOne(@PathVariable Integer id) {
        return map(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lecturer not found")));
    }

    private LecturerSummaryResponse map(Lecturer l) {
        return new LecturerSummaryResponse(l.getId(), l.getFullName(), l.getAccount().getEmail(), l.getFaculty(),
                l.getAcademicRank(), l.getAcademicDegree(), Boolean.TRUE.equals(l.getAccount().getIsActive()));
    }
}
