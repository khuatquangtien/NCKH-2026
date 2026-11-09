package vn.edu.epu.quanlyhoso.paper.service;

import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.epu.quanlyhoso.auth.entity.Account;
import vn.edu.epu.quanlyhoso.common.exception.BusinessException;
import vn.edu.epu.quanlyhoso.common.exception.ProfileIncompleteException;
import vn.edu.epu.quanlyhoso.common.exception.ResourceNotFoundException;
import vn.edu.epu.quanlyhoso.common.security.CurrentUserService;
import vn.edu.epu.quanlyhoso.lecturer.repository.LecturerRepository;
import vn.edu.epu.quanlyhoso.paper.entity.*;
import vn.edu.epu.quanlyhoso.paper.repository.PaperRepository;

@Service
public class PaperServiceImpl implements PaperService {
    private final PaperRepository repository;
    private final CurrentUserService currentUserService;
    private final LecturerRepository lecturerRepository;

    public PaperServiceImpl(PaperRepository repository, CurrentUserService currentUserService,
            LecturerRepository lecturerRepository) {
        this.repository = repository;
        this.currentUserService = currentUserService;
        this.lecturerRepository = lecturerRepository;
    }

    @Override
    @Transactional
    public void declareNewPaper(Paper paper) {
        paper.setId(0);
        paper.setCreatorId(currentLecturerId());
        paper.setStatus(PaperStatus.PENDING);
        repository.save(paper);
    }

    @Override
    @Transactional
    public void editPaper(Paper input) {
        Paper paper = owned(input.getId());
        if (paper.getStatus() != PaperStatus.PENDING) {
            throw new BusinessException("Chỉ có thể chỉnh sửa bài báo khi đang chờ duyệt");
        }
        paper.setTitle(input.getTitle());
        paper.setJournalName(input.getJournalName());
        paper.setPublicationYear(input.getPublicationYear());
        paper.setIssnIsbn(input.getIssnIsbn());
        paper.setJournalRanking(input.getJournalRanking());
        repository.save(paper);
    }

    @Override
    @Transactional
    public void removePaper(int id) {
        Paper paper = owned(id);
        if (paper.getStatus() != PaperStatus.PENDING) {
            throw new BusinessException("Chỉ có thể xóa bài báo khi đang chờ duyệt");
        }
        repository.delete(paper);
    }

    @Override
    @Transactional
    public void verifyPaper(int id, PaperStatus status, String rejectionReason) {
        if (!isAdmin()) {
            throw new AccessDeniedException("Yêu cầu quyền Quản trị viên");
        }
        if (status == PaperStatus.PENDING) {
            throw new BusinessException("Trạng thái xác thực phải là APPROVED hoặc REJECTED");
        }
        Paper paper = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bài báo không tồn tại"));
        paper.setStatus(status);
        if (status == PaperStatus.REJECTED) {
            paper.setRejectionReason(rejectionReason);
        } else {
            paper.setRejectionReason(null);
        }
        repository.save(paper);
    }

    @Override
    public Paper getPaperById(int id) {
        Paper paper = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bài báo không tồn tại"));
        if (!isAdmin() && paper.getCreatorId() != currentLecturerId()) {
            throw new AccessDeniedException("Bạn không có quyền xem bài báo này");
        }
        return paper;
    }

    @Override
    public List<Paper> getPapersByLecturer(int lecturerId) {
        if (!isAdmin() && lecturerId != currentLecturerId()) {
            throw new AccessDeniedException("Bạn chỉ có thể xem bài báo của chính mình");
        }
        return repository.findByCreatorIdOrderByCreatedAtDesc(lecturerId);
    }

    @Override
    public List<Paper> getMyPapers() {
        return repository.findByCreatorIdOrderByCreatedAtDesc(currentLecturerId());
    }

    @Override
    public List<Paper> getAllPendingPapers() {
        if (!isAdmin()) {
            throw new AccessDeniedException("Yêu cầu quyền Quản trị viên");
        }
        return repository.findByStatusOrderByCreatedAtDesc(PaperStatus.PENDING);
    }

    private Paper owned(int id) {
        Paper p = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bài báo không tồn tại"));
        if (p.getCreatorId() != currentLecturerId()) {
            throw new AccessDeniedException("Bạn không phải tác giả của bài báo này");
        }
        return p;
    }

    private int currentLecturerId() {
        Account a = currentUserService.getCurrentAccount();
        return lecturerRepository.findByAccountId(a.getId())
                .orElseThrow(ProfileIncompleteException::new).getId();
    } 

    private boolean isAdmin() {
        return "ADMIN".equals(currentUserService.getCurrentAccount().getRole().getRoleName());
    }
}
