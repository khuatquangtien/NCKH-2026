
package vn.edu.epu.quanlyhoso.lecturer.controller;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.edu.epu.quanlyhoso.lecturer.dto.ProfileRequest;
import vn.edu.epu.quanlyhoso.lecturer.service.ProfileService;

@RestController
@RequestMapping("/api/profile")
@PreAuthorize ("hasAuthority('LECTURER')")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // Xem thông tin cá nhân
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        try {
            return ResponseEntity.ok(profileService.getMyProfile());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Cập nhật thông tin cá nhân
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@Valid @RequestBody ProfileRequest request) {
        return ResponseEntity.ok(profileService.updateMyProfile(request));
    }
}
