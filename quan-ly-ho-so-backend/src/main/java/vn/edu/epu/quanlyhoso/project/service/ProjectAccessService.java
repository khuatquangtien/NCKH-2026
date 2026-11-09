package vn.edu.epu.quanlyhoso.project.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import vn.edu.epu.quanlyhoso.auth.entity.Account;
import vn.edu.epu.quanlyhoso.common.exception.ProfileIncompleteException;
import vn.edu.epu.quanlyhoso.common.exception.ResourceNotFoundException;
import vn.edu.epu.quanlyhoso.common.security.CurrentUserService;
import vn.edu.epu.quanlyhoso.council.repository.*;
import vn.edu.epu.quanlyhoso.lecturer.entity.Lecturer;
import vn.edu.epu.quanlyhoso.lecturer.repository.LecturerRepository;
import vn.edu.epu.quanlyhoso.project.entity.Project;
import vn.edu.epu.quanlyhoso.project.repository.ProjectMemberRepository;

@Service
public class ProjectAccessService {
    private final CurrentUserService current;
    private final LecturerRepository lecturers;
    private final ProjectMemberRepository members;
    private final CouncilAssignmentRepository assignments;
    private final CouncilMemberRepository councilMembers;

    public ProjectAccessService(CurrentUserService c, LecturerRepository l, ProjectMemberRepository m,
            CouncilAssignmentRepository a, CouncilMemberRepository cm) {
        current = c;
        lecturers = l;
        members = m;
        assignments = a;
        councilMembers = cm;
    }

    public void assertCanView(Project project) {
        Account a = current.getCurrentAccount();
        if ("ADMIN".equals(a.getRole().getRoleName()))
            return;
        Lecturer l = lecturer(a);
        boolean participant = project.getLeader().getId().equals(l.getId())
                || members.existsByProject_IdAndLecturer_Id(project.getId(), l.getId());
        boolean council = assignments.findByProjectId(project.getId().longValue())
                .map(x -> councilMembers.existsByCouncilIdAndLecturerId(x.getCouncilId(), l.getId().longValue()))
                .orElse(false);
        if (!participant && !council)
            throw new AccessDeniedException("You do not have access to this project");
    }

    private Lecturer lecturer(Account a) {
        return lecturers.findByAccountId(a.getId())
                .orElseThrow(ProfileIncompleteException::new);
    }
}
