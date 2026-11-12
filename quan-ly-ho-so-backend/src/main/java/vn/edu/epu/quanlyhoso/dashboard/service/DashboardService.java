package vn.edu.epu.quanlyhoso.dashboard.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.edu.epu.quanlyhoso.auth.entity.Account;
import vn.edu.epu.quanlyhoso.auth.repository.AccountRepository;
import vn.edu.epu.quanlyhoso.common.config.RedisConfig;
import vn.edu.epu.quanlyhoso.council.repository.CouncilMemberRepository;
import vn.edu.epu.quanlyhoso.council.repository.CouncilRepository;
import vn.edu.epu.quanlyhoso.dashboard.dto.DashboardResponse;
import vn.edu.epu.quanlyhoso.lecturer.entity.Lecturer;
import vn.edu.epu.quanlyhoso.lecturer.repository.LecturerRepository;
import vn.edu.epu.quanlyhoso.paper.entity.PaperStatus;
import vn.edu.epu.quanlyhoso.paper.repository.PaperRepository;
import vn.edu.epu.quanlyhoso.project.entity.Project;
import vn.edu.epu.quanlyhoso.project.enums.ProjectStatus;
import vn.edu.epu.quanlyhoso.project.repository.ProjectMemberRepository;
import vn.edu.epu.quanlyhoso.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AccountRepository accountsRepository;
    private final ProjectRepository projectsRepository;
    private final ProjectMemberRepository membersRepository;
    private final PaperRepository papersRepository;
    private final CouncilRepository councilsRepository;
    private final CouncilMemberRepository councilMembersRepository;
    private final LecturerRepository lecturersRepository;

    @Cacheable(value = RedisConfig.DASHBOARD_CACHE, key = "#account.id")
    public DashboardResponse getDashboard(Account account) {

        boolean admin = "ADMIN".equals(account.getRole().getRoleName());
        if (admin) {
            Map<String, Long> byMap = new LinkedHashMap<>();
            for (ProjectStatus s : ProjectStatus.values()) {
                byMap.put(s.name(), projectsRepository.countByStatus(s));
            }
            DashboardResponse dashboardResponse = new DashboardResponse(
                    "ADMIN",
                    accountsRepository.count(),
                    accountsRepository.countByIsActiveTrue(),
                    projectsRepository.count(),
                    byMap,
                    papersRepository.count(),
                    papersRepository.countByStatus(PaperStatus.PENDING),
                    councilsRepository.count());

            return dashboardResponse;
        }
        Optional<Lecturer> lectureOptional = lecturersRepository.findByAccountId(account.getId());
        if (lectureOptional.isEmpty()) {
            Map<String, Long> empty = new LinkedHashMap<>();
            for (ProjectStatus s : ProjectStatus.values()) {
                empty.put(s.name(), 0L);

            }
            return new DashboardResponse(
                    "LECTURER",
                    0,
                    0,
                    0,
                    empty,
                    0,
                    0,
                    0);
        }
        int lecturerId = lectureOptional.get().getId();
        Map<Integer, Project> mine = new LinkedHashMap<>();
        projectsRepository
                .findByLeaderId(lecturerId)
                .forEach(projectsRepository -> mine.put(projectsRepository.getId(), projectsRepository));

        membersRepository
                .findByLecturer_Id(lecturerId)
                .forEach(membersRepository -> mine.put(membersRepository.getProject().getId(),
                        membersRepository.getProject()));

        Map<String, Long> byMap = new LinkedHashMap<>();
        for (ProjectStatus s : ProjectStatus.values()) {
            byMap.put(
                    s.name(),
                    mine.values()
                            .stream()
                            .filter(projectsRepository -> projectsRepository.getStatus() == s)
                            .count());

        }
        return new DashboardResponse(
                "LECTURER",
                0,
                0,
                mine.size(),
                byMap,
                papersRepository.countByCreatorId(lecturerId),
                0,
                councilMembersRepository.findByLecturerId((long) lecturerId).size());
    }
}
