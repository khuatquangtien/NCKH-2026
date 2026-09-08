package vn.edu.epu.quanlyhoso.paper.service;

import vn.edu.epu.quanlyhoso.paper.entity.Paper;
import vn.edu.epu.quanlyhoso.paper.entity.PaperStatus;

import java.util.List;

/**
 * Interface PaperService - định nghĩa các nghiệp vụ API liên quan đến bài báo
 * Được sử dụng bởi Giảng viên (declare, edit, remove) và Admin (verify)
 */
public interface PaperService {

    /**
     * Giảng viên khai báo bài báo mới vào hệ thống
     */
    void declareNewPaper(Paper paper);

    /**
     * Giảng viên chỉnh sửa thông tin bài báo
     */
    void editPaper(Paper paper);

    /**
     * Giảng viên xóa bài báo
     */
    void removePaper(int id);

    /**
     * Admin xác thực bài báo - phê duyệt hoặc từ chối
     */
    void verifyPaper(int id, PaperStatus status);

    /**
     * Lấy thông tin chi tiết một bài báo theo id
     */
    Paper getPaperById(int id);

    /**
     * Lấy danh sách bài báo của một giảng viên cụ thể
     */
    List<Paper> getPapersByLecturer(int lecturerId);

    /**
     * Lấy danh sách các bài báo đang chờ duyệt để Admin kiểm tra
     */
    List<Paper> getAllPendingPapers();
}
