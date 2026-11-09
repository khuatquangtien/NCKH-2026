package vn.edu.epu.quanlyhoso.lecturer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileRequest {

    
    @NotBlank(message = "Họ tên không được để trống") 
    @Size (max = 100, message = "Họ và tên không quá 100 kí tự!")
    private String fullName;

    private String academicRank;   
    private String academicDegree; 
    @NotBlank (message = "Khoa không được để trống ")
    private String faculty;        
    private String researchDirection;
}
