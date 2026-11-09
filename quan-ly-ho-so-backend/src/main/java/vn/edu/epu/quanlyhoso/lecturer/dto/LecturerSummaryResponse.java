package vn.edu.epu.quanlyhoso.lecturer.dto;
public record LecturerSummaryResponse(Integer id, String fullName, String email,
        String faculty, String academicRank, String academicDegree, boolean active) {}
