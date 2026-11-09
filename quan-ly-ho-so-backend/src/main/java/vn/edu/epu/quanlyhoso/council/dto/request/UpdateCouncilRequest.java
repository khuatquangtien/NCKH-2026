package vn.edu.epu.quanlyhoso.council.dto.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateCouncilRequest(@NotBlank String name) {}
