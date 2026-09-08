package vn.edu.epu.quanlyhoso.council.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.epu.quanlyhoso.council.entity.CouncilAssignment;

public interface CouncilAssignmentRepository extends JpaRepository<CouncilAssignment, Long> {
    Optional<CouncilAssignment> findByProjectId(Long projectId);
}
