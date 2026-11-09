package vn.edu.epu.quanlyhoso.council.dto.response;
import java.time.LocalDateTime;
import vn.edu.epu.quanlyhoso.council.entity.CouncilRole;
public record MyCouncilResponse(Long id, String name, String status, CouncilRole position, LocalDateTime createdAt) {}
