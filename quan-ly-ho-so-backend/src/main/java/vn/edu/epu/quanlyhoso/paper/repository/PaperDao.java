package vn.edu.epu.quanlyhoso.paper.repository;

import vn.edu.epu.quanlyhoso.paper.entity.Paper;
import vn.edu.epu.quanlyhoso.paper.entity.PaperStatus;

import java.util.List;

/**
 * Interface PaperDao - định nghĩa các phương thức để tương tác với database
 * Không quan tâm đến logic bên trong, chỉ định nghĩa contract
 */
public interface PaperDao {

    /**
     * Thêm mới một bài báo vào database
     */
    void insertPaper(Paper paper);

    /**
     * Cập nhật thông tin bài báo trong database
     */
    void updatePaper(Paper paper);

    /**
     * Xóa một bài báo dựa trên id
     */
    void deletePaper(int id);

    /**
     * Tìm kiếm một bài báo dựa trên id
     */
    Paper findPaperById(int id);

    /**
     * Lấy danh sách tất cả các bài báo
     */
    List<Paper> findAllPapers();

    /**
     * Cập nhật trạng thái của bài báo
     */
    void updatePaperStatus(int id, PaperStatus status);
}
