package vn.edu.epu.quanlyhoso.council.dto.request;
import jakarta.validation.constraints.Pattern;
public record UpdateCouncilStatusRequest(@Pattern(regexp="ACTIVE|COMPLETED|CANCELLED") String status) {}
