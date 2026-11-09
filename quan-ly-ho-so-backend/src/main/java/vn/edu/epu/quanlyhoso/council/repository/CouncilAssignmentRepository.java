package vn.edu.epu.quanlyhoso.council.repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.epu.quanlyhoso.council.entity.CouncilAssignment;

public interface CouncilAssignmentRepository extends JpaRepository<CouncilAssignment, Long> {
    Optional<CouncilAssignment> findByProjectId(Long projectId);
    List<CouncilAssignment> findByCouncilId(Long councilId);
    void deleteByCouncilIdAndProjectId(Long councilId, Long projectId);
}
