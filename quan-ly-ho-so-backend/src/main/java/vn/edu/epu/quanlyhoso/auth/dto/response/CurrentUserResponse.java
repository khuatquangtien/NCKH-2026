package vn.edu.epu.quanlyhoso.auth.dto.response;
public record CurrentUserResponse(Integer accountId, String username, String email,
        String role, Integer lecturerId, String fullName, String faculty) {}
