package vn.edu.epu.quanlyhoso.account.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.edu.epu.quanlyhoso.account.service.AccountService;
import vn.edu.epu.quanlyhoso.admin.dto.request.UpdateAccountRequest;
import vn.edu.epu.quanlyhoso.auth.dto.response.AccountResponse;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // Xóa tài khoản (ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        accountService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Lấy danh sách tất cả tài khoản
    @GetMapping("/users")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    // Sửa thông tin tài khoản
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable Integer id, @RequestBody UpdateAccountRequest request) {
        try {
            accountService.updateAccount(id, request);
            return ResponseEntity.ok("Đã cập nhật tài khoản");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
