package vn.edu.epu.quanlyhoso.paper.service;

import vn.edu.epu.quanlyhoso.paper.entity.Paper;
import vn.edu.epu.quanlyhoso.paper.entity.PaperStatus;
import vn.edu.epu.quanlyhoso.paper.repository.PaperDao;
import vn.edu.epu.quanlyhoso.paper.repository.PaperDaoImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Class PaperServiceImpl - triển khai các nghiệp vụ liên quan đến bài báo
 * Thực thi logic chính xác cho các hoạt động của Giảng viên và Admin
 */
public class PaperServiceImpl implements PaperService {
    
    private PaperDao paperDao = new PaperDaoImpl();

    @Override
    public void declareNewPaper(Paper paper) {
        // Đặt trạng thái mặc định là PENDING khi khai báo bài báo mới
        paper.setStatus(PaperStatus.PENDING);
        paperDao.insertPaper(paper);
    }

    @Override
    public void editPaper(Paper paper) {
        // Kiểm tra bài báo cũ
        Paper oldPaper = paperDao.findPaperById(paper.getId());
        
        // Chỉ cho phép chỉnh sửa nếu bài báo có trạng thái PENDING
        if (oldPaper != null && oldPaper.getStatus() == PaperStatus.PENDING) {
            paperDao.updatePaper(paper);
        } else {
            System.out.println("Lỗi: Chỉ có thể chỉnh sửa bài báo ở trạng thái PENDING");
        }
    }

    @Override
    public void removePaper(int id) {
        // Kiểm tra trạng thái bài báo
        Paper paper = paperDao.findPaperById(id);
        
        // Chỉ cho phép xóa nếu bài báo có trạng thái PENDING
        if (paper != null && paper.getStatus() == PaperStatus.PENDING) {
            paperDao.deletePaper(id);
        } else {
            System.out.println("Lỗi: Chỉ có thể xóa bài báo ở trạng thái PENDING");
        }
    }

    @Override
    public void verifyPaper(int id, PaperStatus status) {
        // Cập nhật trạng thái bài báo (Admin phê duyệt hoặc từ chối)
        paperDao.updatePaperStatus(id, status);
    }

    @Override
    public Paper getPaperById(int id) {
        try {
            // Lấy thông tin bài báo theo id
            return paperDao.findPaperById(id);
        } catch (Exception e) {
            System.out.println("Lỗi khi lấy thông tin bài báo: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Paper> getPapersByLecturer(int lecturerId) {
        try {
            // Lấy tất cả bài báo rồi lọc theo lecturerId
            List<Paper> allPapers = paperDao.findAllPapers();
            List<Paper> lecturerPapers = new ArrayList<>();
            
            for (Paper paper : allPapers) {
                if (paper.getCreatorId() == lecturerId) {
                    lecturerPapers.add(paper);
                }
            }
            
            return lecturerPapers;
        } catch (Exception e) {
            System.out.println("Lỗi khi lấy bài báo của giảng viên: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Paper> getAllPendingPapers() {
        try {
            // Lấy tất cả bài báo rồi lọc những bài có trạng thái PENDING
            List<Paper> allPapers = paperDao.findAllPapers();
            List<Paper> pendingPapers = new ArrayList<>();
            
            for (Paper paper : allPapers) {
                if (paper.getStatus() == PaperStatus.PENDING) {
                    pendingPapers.add(paper);
                }
            }
            
            return pendingPapers;
        } catch (Exception e) {
            System.out.println("Lỗi khi lấy bài báo chờ duyệt: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
