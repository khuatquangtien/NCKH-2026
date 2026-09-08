package vn.edu.epu.quanlyhoso.paper.controller;

import vn.edu.epu.quanlyhoso.paper.entity.Paper;
import vn.edu.epu.quanlyhoso.paper.entity.PaperStatus;
import vn.edu.epu.quanlyhoso.paper.service.PaperService;
import vn.edu.epu.quanlyhoso.paper.service.PaperServiceImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Class PaperController - Tiếp nhận yêu cầu từ HTTP Client (Thunder Client/Postman)
 * Lộ trình các API được định tuyến qua Spring Boot REST Controller
 */
@RestController
@RequestMapping("/api/papers")
public class PaperController {
    
    private PaperService paperService = new PaperServiceImpl();

    /**
     * API: Giảng viên khai báo bài báo mới
     * URL: POST http://localhost:8080/api/papers
     */
    @PostMapping
    public ResponseEntity<?> declareNewPaper(@RequestBody Paper paper) {
        try {
            // TỰ ĐỘNG GÁN TRẠNG THÁI PENDING KHI KHAI BÁO MỚI
            paper.setStatus(PaperStatus.PENDING);

            paperService.declareNewPaper(paper);
            return ResponseEntity.ok("Thành công: Bài báo đã được khai báo");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi khai báo bài báo: " + e.getMessage());
        }
    }

    /**
     * API: Giảng viên chỉnh sửa bài báo
     * URL: PUT http://localhost:8080/api/papers/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> editPaper(@PathVariable int id, @RequestBody Paper paper) {
        try {
            // Đảm bảo ID từ URL được gán chính xác vào đối tượng Model
            paper.setId(id); 
            paperService.editPaper(paper);
            return ResponseEntity.ok("Thành công: Bài báo đã được cập nhật");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi chỉnh sửa bài báo: " + e.getMessage());
        }
    }

    /**
     * API: Giảng viên xóa bài báo
     * URL: DELETE http://localhost:8080/api/papers/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removePaper(@PathVariable int id) {
        try {
            paperService.removePaper(id);
            return ResponseEntity.ok("Thành công: Bài báo đã được xóa");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa bài báo: " + e.getMessage());
        }
    }

    /**
     * API: Admin xác thực bài báo (phê duyệt/từ chối)
     * URL: PATCH http://localhost:8080/api/papers/{id}/verify?status=APPROVED hoặc REFUSED
     */
    @PatchMapping("/{id}/verify")
    public ResponseEntity<?> verifyPaper(@PathVariable int id, @RequestParam PaperStatus status) {
        try {
            paperService.verifyPaper(id, status);
            return ResponseEntity.ok("Thành công: Bài báo đã được xác thực với trạng thái " + status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xác thực bài báo: " + e.getMessage());
        }
    }

    /**
     * API: Lấy thông tin chi tiết một bài báo
     * URL: GET http://localhost:8080/api/papers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaperById(@PathVariable int id) {
        try {
            Paper paper = paperService.getPaperById(id);
            if (paper != null) {
                return ResponseEntity.ok(paper);
            } else {
                return ResponseEntity.status(404).body("Lỗi: Bài báo không tồn tại");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lấy thông tin bài báo: " + e.getMessage());
        }
    }

    /**
     * API: Lấy danh sách bài báo của một giảng viên
     * URL: GET http://localhost:8080/api/papers/lecturer/{lecturerId}
     */
    @GetMapping("/lecturer/{lecturerId}")
    public ResponseEntity<?> getPapersByLecturer(@PathVariable int lecturerId) {
        try {
            List<Paper> papers = paperService.getPapersByLecturer(lecturerId);
            if (papers != null && !papers.isEmpty()) {
                return ResponseEntity.ok(papers);
            } else {
                return ResponseEntity.ok("Thông báo: Giảng viên không có bài báo nào");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lấy danh sách bài báo: " + e.getMessage());
        }
    }

    /**
     * API: Lấy danh sách bài báo chờ duyệt (dành cho Admin)
     * URL: GET http://localhost:8080/api/papers/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getAllPendingPapers() {
        try {
            List<Paper> papers = paperService.getAllPendingPapers();
            if (papers != null && !papers.isEmpty()) {
                return ResponseEntity.ok(papers);
            } else {
                return ResponseEntity.ok("Thông báo: Không có bài báo nào chờ duyệt");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lấy danh sách bài báo chờ duyệt: " + e.getMessage());
        }
    }
}