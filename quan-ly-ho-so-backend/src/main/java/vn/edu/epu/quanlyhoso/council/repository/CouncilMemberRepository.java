package vn.edu.epu.quanlyhoso.council.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.epu.quanlyhoso.council.entity.CouncilMember;
import vn.edu.epu.quanlyhoso.council.entity.CouncilMemberId;

public interface CouncilMemberRepository extends JpaRepository<CouncilMember, CouncilMemberId> {
    List<CouncilMember> findByCouncilId(Long councilId);
}
