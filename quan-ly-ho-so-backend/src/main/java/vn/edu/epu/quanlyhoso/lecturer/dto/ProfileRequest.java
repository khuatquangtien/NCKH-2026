package vn.edu.epu.quanlyhoso.lecturer.dto;

import lombok.Data;

@Data
public class ProfileRequest {
    private String fullName;
    private String academicRank;   
    private String academicDegree; 
    private String faculty;        
    private String researchDirection;
}
