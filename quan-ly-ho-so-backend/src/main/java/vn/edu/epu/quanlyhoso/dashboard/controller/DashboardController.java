package vn.edu.epu.quanlyhoso.dashboard.controller;

import java.util.*;
import org.springframework.web.bind.annotation.*;
import vn.edu.epu.quanlyhoso.auth.entity.Account;
import vn.edu.epu.quanlyhoso.auth.repository.AccountRepository;
import vn.edu.epu.quanlyhoso.common.exception.ProfileIncompleteException;
import vn.edu.epu.quanlyhoso.common.exception.ResourceNotFoundException;
import vn.edu.epu.quanlyhoso.common.security.CurrentUserService;
import vn.edu.epu.quanlyhoso.council.repository.*;
import vn.edu.epu.quanlyhoso.dashboard.dto.DashboardResponse;
import vn.edu.epu.quanlyhoso.lecturer.entity.Lecturer;
import vn.edu.epu.quanlyhoso.lecturer.repository.LecturerRepository;
import vn.edu.epu.quanlyhoso.paper.entity.PaperStatus;
import vn.edu.epu.quanlyhoso.paper.repository.PaperRepository;
import vn.edu.epu.quanlyhoso.project.entity.Project;
import vn.edu.epu.quanlyhoso.project.enums.ProjectStatus;
import vn.edu.epu.quanlyhoso.project.repository.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final AccountRepository accounts;
    private final ProjectRepository projects;
    private final ProjectMemberRepository members;
    private final PaperRepository papers;
    private final CouncilRepository councils;
    private final CouncilMemberRepository councilMembers;
    private final LecturerRepository lecturers;
    private final CurrentUserService current;

    public DashboardController(AccountRepository a, ProjectRepository p, ProjectMemberRepository m,
            PaperRepository paper,
            CouncilRepository c, CouncilMemberRepository cm, LecturerRepository l, CurrentUserService current) {
        accounts = a;
        projects = p;
        members = m;
        papers = paper;
        councils = c;
        councilMembers = cm;
        lecturers = l;
        this.current = current;
    }

    @GetMapping
    public DashboardResponse get() {
        Account account = current.getCurrentAccount();
        boolean admin = "ADMIN".equals(account.getRole().getRoleName());
        if (admin) {
            Map<String, Long> by = new LinkedHashMap<>();
            for (ProjectStatus s : ProjectStatus.values())
                by.put(s.name(), projects.countByStatus(s));
            return new DashboardResponse("ADMIN",
                    accounts.count(),
                    accounts.countByIsActiveTrue(),
                    projects.count(),
                    by,
                    papers.count(),
                    papers.countByStatus(PaperStatus.PENDING),
                    councils.count());
        }
        Optional<Lecturer> lecturerOpt = lecturers.findByAccountId(account.getId());
        if (lecturerOpt.isEmpty()) {
            Map<String, Long> empty = new LinkedHashMap<>();
            for (ProjectStatus s : ProjectStatus.values())
                empty.put(s.name(), 0L);
            return new DashboardResponse("LECTURER", 0, 0, 0, empty, 0, 0, 0);
        }
        int lecturerId = lecturerOpt.get().getId();

        Map<Integer, Project> mine = new LinkedHashMap<>();
        projects.findByLeaderId(lecturerId).forEach(p -> mine.put(p.getId(), p));
        members.findByLecturer_Id(lecturerId).forEach(m -> mine.put(m.getProject().getId(), m.getProject()));
        Map<String, Long> by = new LinkedHashMap<>();
        for (ProjectStatus s : ProjectStatus.values())
            by.put(s.name(), mine.values().stream().filter(p -> p.getStatus() == s).count());
        return new DashboardResponse("LECTURER", 0, 0, mine.size(), by, papers.countByCreatorId(lecturerId), 0,
                councilMembers.findByLecturerId((long) lecturerId).size());
    }
}
