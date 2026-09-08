package vn.edu.epu.quanlyhoso.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.edu.epu.quanlyhoso.admin.service.AdminService;
import vn.edu.epu.quanlyhoso.auth.dto.request.UpdateRoleRequest;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
	
    private final AdminService adminService;
    
    @PutMapping("/assign-role")
    public ResponseEntity<?> assignRole(@RequestBody UpdateRoleRequest request) {
        try {
            String message = adminService.assignRole(request.getUsername(), request.getRoleId());
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    
}
