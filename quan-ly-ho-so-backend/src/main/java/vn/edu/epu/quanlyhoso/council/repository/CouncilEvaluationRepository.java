package vn.edu.epu.quanlyhoso.council.repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.epu.quanlyhoso.council.entity.CouncilEvaluation;

public interface CouncilEvaluationRepository extends JpaRepository<CouncilEvaluation, Long> {
    List<CouncilEvaluation> findByProjectId(Long projectId);
    List<CouncilEvaluation> findByCouncilId(Long councilId);
    Optional<CouncilEvaluation> findByCouncilIdAndProjectIdAndEvaluatorId(Long councilId, Long projectId, Long evaluatorId);
}
