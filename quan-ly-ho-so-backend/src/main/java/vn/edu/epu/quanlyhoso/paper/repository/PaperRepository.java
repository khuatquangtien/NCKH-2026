package vn.edu.epu.quanlyhoso.paper.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.epu.quanlyhoso.paper.entity.*;
public interface PaperRepository extends JpaRepository<Paper, Integer> {
    List<Paper> findByCreatorIdOrderByCreatedAtDesc(int creatorId);
    List<Paper> findByStatusOrderByCreatedAtDesc(PaperStatus status);
    long countByStatus(PaperStatus status);
    long countByCreatorId(int creatorId);
}
